use std::{sync::Arc, time::Duration};
use std::collections::HashMap;

use actix_web_lab::{
    sse::{self, Sse},
    util::InfallibleStream,
};
use parking_lot::Mutex;
use tokio::sync::mpsc;
use tokio::time::interval;
use tokio_stream::wrappers::ReceiverStream;

use crate::common::response::{BaseResponse, ErrorResponse};

#[derive(Debug)]
pub struct SseBroadcaster {
    inner: Mutex<SseBroadcasterInner>,
}

#[derive(Debug, Clone, Default)]
pub struct SseBroadcasterInner {
    clients: HashMap<String, mpsc::Sender<sse::Event>>,
}

impl SseBroadcaster {
    pub fn create() -> Arc<Self> {
        let this = Arc::new(SseBroadcaster {
            inner: Mutex::new(SseBroadcasterInner::default())
        });
        SseBroadcaster::spawn_ping(Arc::clone(&this));
        this
    }

    /// Pings clients every 10 seconds to see if they are alive and remove them from the broadcast
    /// list if not.
    fn spawn_ping(this: Arc<Self>) {
        actix_web::rt::spawn(async move {
            let mut interval = interval(Duration::from_secs(10));

            loop {
                interval.tick().await;
                this.remove_stale_clients().await;
            }
        });
    }

    /// Removes all non-responsive clients from broadcast list.
    async fn remove_stale_clients(&self) {
        let clients = self
            .inner.lock().clients.clone();

        let mut ok_clients = HashMap::new();

        for (key, client) in clients {
            if client
                .send(sse::Event::Comment("ping".into()))
                .await
                .is_ok()
            {
                ok_clients.insert(key, client.clone());
            }
        }

        self.inner.lock().clients = ok_clients;
    }
    /// Registers client with broadcaster, returning an SSE response body.
    pub async fn new_client(&self, key: &str) -> Sse<InfallibleStream<ReceiverStream<sse::Event>>> {
        let (tx, rx) = mpsc::channel(10);

        tx.send(sse::Data::new_json(
            BaseResponse::success(200, Some(format!("registered user {}",key)), "Connected to the server".to_string())
        ).unwrap().into()).await.unwrap();

        self.inner.lock().clients.insert(key.to_string(), tx);

        Sse::from_infallible_receiver(rx)
    }

    pub async fn reject_client(&self) -> Sse<InfallibleStream<ReceiverStream<sse::Event>>> {
        let (tx, rx) = mpsc::channel(10);
        tx.send(sse::Data::new_json(
            ErrorResponse::bad_request(200, "connection was rejected by owner, you must provide key".to_string())
        ).unwrap().event("closed").into()).await.unwrap();
        Sse::from_infallible_receiver(rx)
    }
    /// Broadcasts `msg` to all clients.
    pub async fn broadcast(&self, topic: &str, msg: &str) {
        let clients = self.inner.lock().clients.clone();
        for (_, value) in clients {
            let _ = value
                .send(sse::Event::Data(
                    sse::Data::new_json(
                        BaseResponse::success(200, Some(msg), "ini message".to_string())
                    ).unwrap().event(topic))
                ).await;
        }
    }

    pub async fn send_to(&self,to: &str, topic: &str, msg: &str)->Option<String> {
        let clients = self.inner.lock().clients.clone();
        let search_client = clients.get(to);
        if search_client.is_none(){
            return None
        }
        let _ = search_client.unwrap().send(
            sse::Event::Data(
                sse::Data::new_json(
                    BaseResponse::success(200, Some(msg), "ini message".to_string())
                ).unwrap().event(topic)
            )
        ).await;

        Some(String::from(""))
    }
}
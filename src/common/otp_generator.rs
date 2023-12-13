use std::time::{SystemTime, UNIX_EPOCH};
use rand::{Rng, thread_rng};

pub fn generate_otp()->u32{
    let now = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs() / 30;
    let mut rng = thread_rng();
    let random_number:u32 = rng
        .gen_range(0..10000);
    let otp = (now as u32 ^ random_number) % 10000;
    otp
}
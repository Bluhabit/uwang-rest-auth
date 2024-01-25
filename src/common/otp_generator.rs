use std::time::{SystemTime, UNIX_EPOCH};
use rand::{Rng, thread_rng};
use validator::HasLen;

pub fn generate_otp()->String{
    let now = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .unwrap()
        .as_secs() / 30;
    let mut rng = thread_rng();
    let random_number:u32 = rng
        .gen_range(0..10000);
    let otp = (now as u32 ^ random_number) % 10000;
    if otp.to_string().length() < 4{
        return format!("{}{}",otp,rng.gen_range(0..10))
    }
    format!("{}",otp)
}
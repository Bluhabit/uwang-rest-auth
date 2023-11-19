use validator::ValidationErrors;

pub fn get_readable_validation_message(
    err: ValidationErrors
) -> String {
    err.field_errors().into_iter()
        .map(|(field,b)| {
            let message: String = b.into_iter().map(|er| {
                // let message = match er.clone().message {
                //     Some(val) => val.to_string(),
                //     None => String::from("<no message>")
                // };
                return format!("{}", er.message.clone().unwrap().to_string());
            }).collect();
            return format!("Field {} {}", field,message);
        }).collect()
}
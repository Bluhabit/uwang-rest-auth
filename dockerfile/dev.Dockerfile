FROM rustlang/rust:nightly AS builder
WORKDIR /workdir
COPY ./Cargo.toml ./Cargo.lock ./
COPY ./migration ./migration
COPY ./src ./src
RUN cargo +nightly build --release

FROM debian:bullseye-slim
COPY --from=builder /workdir/target/release/uwang-rest-api /usr/local/bin

RUN apt-get update && apt

EXPOSE 7005
ENTRYPOINT ["/usr/local/bin/uwang-rest-api"]
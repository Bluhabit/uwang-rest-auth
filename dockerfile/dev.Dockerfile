FROM rustlang/rust:nightly AS builder
WORKDIR /workdir
COPY ./Cargo.toml ./Cargo.lock ./
COPY ./migration ./migration
COPY ./src ./src
RUN cargo +nightly build --release

FROM debian:bullseye
EXPOSE 7005
COPY --from=0 /workdir/target/release/uwang-rest-api /usr/local/bin
ENTRYPOINT ["/usr/local/bin/uwang-rest-api"]
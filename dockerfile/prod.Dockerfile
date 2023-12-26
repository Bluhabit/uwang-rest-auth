FROM rustlang/rust:nightly AS builder
WORKDIR /workdir
COPY ./Cargo.toml ./Cargo.lock ./
COPY ./migration ./migration
COPY ./templates ./templates
COPY ./src ./src
RUN cargo +nightly build --release

FROM debian:bullseye
COPY --from=builder /workdir/target/release/uwang-rest-api /usr/local/bin
COPY --from=builder ./workdir/templates ./usr/local/bin/templates
EXPOSE 7001
ENTRYPOINT ["/usr/local/bin/uwang-rest-api"]
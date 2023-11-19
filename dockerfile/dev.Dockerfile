FROM rust:1.49 as build

# create a new empty shell project
RUN USER=root cargo new --bin holodeck
WORKDIR /uwang-rest-api

# copy over your manifests
COPY ./Cargo.lock ./Cargo.lock
COPY ./Cargo.toml ./Cargo.toml

# this build step will cache your dependencies
RUN cargo build --release
RUN rm src/*.rs

# copy your source tree
COPY ./src ./src

# build for release
RUN rm ./target/release/deps/uwang-rest-api*
RUN cargo build --release

# our final base
FROM rust:1.49-slim-buster

# copy the build artifact from the build stage
COPY --from=build /uwang-rest-api/target/release/uwang-rest-api .

# set the startup command to run your binary
CMD ["./uwang-rest-api"]
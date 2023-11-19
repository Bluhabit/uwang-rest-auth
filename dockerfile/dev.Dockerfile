FROM rust:latest as build

# create a new empty shell project
RUN USER=root cargo new --bin uwang-rest-api
WORKDIR /uwang-rest-api

# copy over your manifests
COPY ./Cargo.lock ./Cargo.lock
COPY ./Cargo.toml ./Cargo.toml

# copy your source tree
COPY ./src ./src

# build for release
RUN cargo build --release

# our final base
FROM rust:slim-buster

# copy the build artifact from the build stage
COPY --from=build /uwang-rest-api/target/release/uwang-rest-api .

# set the startup command to run your binary
CMD ["./uwang-rest-api"]
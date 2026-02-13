FROM golang:1.25-alpine AS builder
WORKDIR /app

COPY sorting-service/ .

RUN go mod tidy
RUN go build -o main .

FROM alpine:latest
WORKDIR /root/
COPY --from=builder /app/main .

EXPOSE 50051

CMD ["./main"]
# Phase 1: Kompilieren
FROM golang:1.21-alpine AS builder
WORKDIR /app
# Kopiere Go-Dateien und baue das Binary
COPY sorting-service/ .
RUN go mod init sorting-service || true
RUN go build -o main .

# Phase 2: Schlankes Laufzeit-Image
FROM alpine:latest
WORKDIR /root/
COPY --from=builder /app/main .
EXPOSE 8081
CMD ["./main"]
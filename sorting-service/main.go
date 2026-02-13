package main

import (
	"context"
	"log"
	"net"
	"sort"
	"sorting-service/sortingpb"

	"google.golang.org/grpc"
	"google.golang.org/grpc/reflection"
)

type server struct {
	sortingpb.UnimplementedPersonSorterServer
}

func (s *server) Sort(ctx context.Context, req *sortingpb.SortRequest) (*sortingpb.SortResponse, error) {
	log.Printf("Receiving gRPC-Response: Sort %d persons", len(req.Persons))

	sortedPersons := make([]*sortingpb.PersonMsg, len(req.Persons))
	copy(sortedPersons, req.Persons)

	sort.Slice(sortedPersons, func(i, j int) bool {
		return sortedPersons[i].Age < sortedPersons[j].Age
	})

	log.Println("sorting completed successful.")

	return &sortingpb.SortResponse{
		Persons: sortedPersons,
	}, nil
}

func main() {
	lis, err := net.Listen("tcp", "0.0.0.0:50051")
	if err != nil {
		log.Fatalf("cant open port 50051: %v", err)
	}

	s := grpc.NewServer()

	sortingpb.RegisterPersonSorterServer(s, &server{})

	reflection.Register(s)

	log.Println("gRPC Service opened at port 50051...")

	if err := s.Serve(lis); err != nil {
		log.Fatalf("Error running server: %v", err)
	}
}

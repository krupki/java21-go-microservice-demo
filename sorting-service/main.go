package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"sort"
)

// Die Struktur muss exakt zum JSON aus deiner Java-App passen
type Person struct {
	ID   int    `json:"id"`
	Name string `json:"name"`
	Age  int    `json:"age"`
}

func sortHandler(w http.ResponseWriter, r *http.Request) {
	// Nur POST erlauben
	if r.Method != http.MethodPost {
		http.Error(w, "Nur POST ist erlaubt", http.StatusMethodNotAllowed)
		return
	}

	var persons []Person
	// JSON aus dem Request-Body in Slice dekodieren
	err := json.NewDecoder(r.Body).Decode(&persons)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	// Sortier-Logik: In-place Sortierung nach Alter
	sort.Slice(persons, func(i, j int) bool {
		return persons[i].Age < persons[j].Age
	})

	// Rückgabe als JSON
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(persons)
}

func main() {
	http.HandleFunc("/sort", sortHandler)
	fmt.Println("Go-Sorter läuft auf Port 8081...")
	http.ListenAndServe(":8081", nil)
}
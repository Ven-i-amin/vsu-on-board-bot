package handler

import (
	"encoding/json"
	"net/http"

	. "awesomeProject/model/input"
	. "awesomeProject/usecase/file"
)

type IFileHandler interface {
	HandleGet(w http.ResponseWriter, r *http.Request)
	HandlePost(w http.ResponseWriter, r *http.Request)
}

type FileHandler struct {
	UseCase IFileUsecase
}

func (f FileHandler) HandleGet(w http.ResponseWriter, r *http.Request) {
	fileName := r.URL.Query().Get("name")
	fileData, err := f.UseCase.GetFile(fileName)

	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
	}

	_, err = w.Write([]byte(fileData))

	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}

func (f FileHandler) HandlePost(w http.ResponseWriter, r *http.Request) {
	var in FileInput
	fileName := r.URL.Query().Get("name")

	if err := json.NewDecoder(r.Body).Decode(&in); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
	}

	if err := f.UseCase.SaveFile(fileName, in.Data); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
	}
}

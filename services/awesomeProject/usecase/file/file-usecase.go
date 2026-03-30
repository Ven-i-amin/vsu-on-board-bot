package file

import (
	"os"
)

type IFileUsecase interface {
	GetFile(filename string) (string, error)
	SaveFile(filename string, data string) error
}

type FileUseCase struct {
}

func (f *FileUseCase) GetFile(filename string) (string, error) {
	data, err := os.ReadFile(filename + ".txt")

	return string(data), err
}

func (f *FileUseCase) SaveFile(filename string, data string) error {
	err := os.WriteFile(filename+".txt", []byte(data), 0644)

	return err
}

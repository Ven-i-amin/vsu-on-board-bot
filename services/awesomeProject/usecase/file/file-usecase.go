package file

import "os"

type FileUsecase interface {
	GetFile(filename string) *string
	SaveFile(filename string, data string)
}

type fileUsecase struct {

}

func (f fileUsecase) GetFile(filename string) *string {
	data, err := os.ReadFile(filename + ".txt")

	if err != nil {
		return nil
	}
}
package g3.viewchoosephoto.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.repo.ILocalMediaRepository

class PhotoPickerViewModel(private val localMediaRepository: ILocalMediaRepository) : ViewModel() {

    private var listLocalAlbum: MutableLiveData<List<AlbumImage>> = localMediaRepository.getAllLocalAlbums()

}
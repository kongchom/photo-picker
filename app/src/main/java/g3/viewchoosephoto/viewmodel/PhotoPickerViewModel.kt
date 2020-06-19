package g3.viewchoosephoto.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.model.LocalImage
import g3.viewchoosephoto.repo.ILocalMediaRepository
import javax.inject.Inject

class PhotoPickerViewModel @Inject constructor(
    private val localMediaRepository: ILocalMediaRepository
) : ViewModel() {

    var listLocalAlbum = MutableLiveData<List<AlbumImage>>().apply {
        value = emptyList()
    }
    var listCameraImage = MutableLiveData<List<LocalImage>>().apply {
        value = emptyList()
    }


    fun loadData() {
        val listAlbum = localMediaRepository.getAllLocalAlbums()
        val cameraImages = listAlbum[0].localImages
        listCameraImage.value = ArrayList(cameraImages)
    }

    fun reloadData() {
        val listAlbum = localMediaRepository.getAllLocalAlbums()
        val cameraImages = listAlbum[0].localImages
        listCameraImage.postValue(cameraImages)
    }

}
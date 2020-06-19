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
    var mListAlbum: ArrayList<AlbumImage> = ArrayList()
    var listLocalAlbum = MutableLiveData<List<AlbumImage>>().apply {
        value = emptyList()
    }

    var listCameraImage = MutableLiveData<List<LocalImage>>().apply {
        value = emptyList()
    }

    fun loadData() {
        val listAlbum = localMediaRepository.getAllLocalAlbums()
        mListAlbum = ArrayList(listAlbum)
        listLocalAlbum.value = ArrayList(listAlbum)
    }

    fun reloadData() {
        val listAlbum = localMediaRepository.getAllLocalAlbums()
        mListAlbum = ArrayList(listAlbum)
        listLocalAlbum.postValue(listAlbum)
    }

}
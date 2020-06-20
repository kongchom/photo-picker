package g3.viewchoosephoto.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import g3.viewchoosephoto.model.AlbumImage
import g3.viewchoosephoto.model.LocalImage
import g3.viewchoosephoto.notifyObserver
import g3.viewchoosephoto.repo.ILocalMediaRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PhotoPickerViewModel @Inject constructor(
    private val localMediaRepository: ILocalMediaRepository
) : ViewModel() {

    var listLocalAlbum = MutableLiveData<MutableList<AlbumImage>>()
    var listPhotoChose = MutableLiveData<MutableList<LocalImage>>()
    var sizeOfListPhotoChose = MutableLiveData<Int>().apply {
        value = 0
    }
    var mTargetFile:File = File("")
    private var currentFolder: Int = 0
    init {
        listLocalAlbum.value = ArrayList()
        listPhotoChose.value = ArrayList()
        loadData()
    }

    /**
     *Call when viewModel created with activity and when get permission to read device images.
     */
    fun loadData() {
        val listAlbum = localMediaRepository.getAllLocalAlbums()
        listLocalAlbum.value?.addAll(listAlbum)
        listLocalAlbum.notifyObserver()
    }

    fun reloadImageList() {
        val listAlbum = localMediaRepository.getAllLocalAlbums()
        listLocalAlbum.postValue(listAlbum as MutableList<AlbumImage>?)
    }

   fun setFolderPosition(position: Int) {
        currentFolder = position
    }

    fun addImageToPhotoChoseList(position: Int) {
        if (sizeOfListPhotoChose.value!! < 60) {
            listPhotoChose.value?.add(listLocalAlbum.value!![currentFolder].localImages[position])
            sizeOfListPhotoChose.value = listPhotoChose.value?.size
            listPhotoChose.notifyObserver()
        }
    }

    fun removeImage(position: Int) {
        listPhotoChose.value?.removeAt(position)
        sizeOfListPhotoChose.value = listPhotoChose.value?.size
    }

    fun addImageFromCamera(localImagePath: String, isSavingImage: Boolean) {
        val image = LocalImage()
        image.path = localImagePath
        if (isSavingImage) {
            val targetFile =
                File(listLocalAlbum.value!![0].path + System.currentTimeMillis() + ".jpg")
            Timber.d("congnm path name ${listLocalAlbum.value!![0].path}")
            File(localImagePath).copyTo(
                target = targetFile,
                overwrite = false
            )
            mTargetFile = targetFile
        } else {
            listPhotoChose.value?.add(image)
            sizeOfListPhotoChose.value = listPhotoChose.value?.size
            listPhotoChose.notifyObserver()
        }
    }
}
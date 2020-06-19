package g3.viewchoosephoto.di

import dagger.Component
import g3.viewchoosephoto.ui.PhotoPickerActivity
import javax.inject.Singleton


@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    fun inject(activity: PhotoPickerActivity)
}
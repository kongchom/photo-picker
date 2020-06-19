package g3.viewchoosephoto.di

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import g3.viewchoosephoto.repo.ILocalMediaRepository
import g3.viewchoosephoto.repo.LocalMediaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AppModule constructor(
    private val context: Context
) {

    @Provides
    @Singleton
    fun provideContext(): Context = context

    @Provides
    @Singleton
    fun provideLocalMediaRepository(): ILocalMediaRepository {
        return LocalMediaRepository(context)
    }

}
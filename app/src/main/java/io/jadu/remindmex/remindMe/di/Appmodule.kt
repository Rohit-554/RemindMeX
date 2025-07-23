package io.jadu.remindmex.remindMe.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.jadu.remindmex.remindMe.data.repositoryImpl.ReminderRepositoryImpl
import io.jadu.remindmex.remindMe.domain.repository.ReminderRepository
import io.jadu.remindmex.remindMe.domain.usecase.AddReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.DeleteReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.GetRemindersUseCase
import io.jadu.remindmex.remindMe.domain.usecase.UpdateReminderUseCase
import io.jadu.remindmex.remindMe.presentation.viewModels.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }        //provides a single instance through out the app
    single<ReminderRepository> {
        ReminderRepositoryImpl(
            firestore = get(),
            storage = get()
        )
    }
    factory { AddReminderUseCase(get()) }   //provides a new instance everytime
    factory { GetRemindersUseCase(get()) }
    factory { UpdateReminderUseCase(get()) }
    factory { DeleteReminderUseCase(get()) }

    viewModel { LoginViewModel() }
    /*viewModel {  }*/ //ReminderViewModel
}
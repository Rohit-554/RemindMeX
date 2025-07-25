package io.jadu.remindmex.remindMe.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.jadu.remindmex.remindMe.data.repositoryImpl.ReminderRepositoryImpl
import io.jadu.remindmex.remindMe.domain.repository.GeminiApiService
import io.jadu.remindmex.remindMe.domain.repository.ReminderRepository
import io.jadu.remindmex.remindMe.domain.usecase.AddReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.DeleteReminderUseCase
import io.jadu.remindmex.remindMe.domain.usecase.GenerateGreetingUseCase
import io.jadu.remindmex.remindMe.domain.usecase.GetRemindersUseCase
import io.jadu.remindmex.remindMe.domain.usecase.UpdateReminderUseCase
import io.jadu.remindmex.remindMe.presentation.viewModels.LoginViewModel
import io.jadu.remindmex.remindMe.presentation.viewModels.ReminderViewModel
import io.jadu.remindmex.remindMe.presentation.viewModels.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    single {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
    single {
        val apiKey = ""
        GenerateGreetingUseCase(get(), apiKey)
    }
    single<ReminderRepository> {
        ReminderRepositoryImpl(
            firestore = get(),
            storage = get()
        )
    }
    factory { AddReminderUseCase(get()) }
    factory { GetRemindersUseCase(get()) }
    factory { UpdateReminderUseCase(get()) }
    factory { DeleteReminderUseCase(get()) }


    viewModel { LoginViewModel() }
    single { ThemeViewModel() }
    viewModel { ReminderViewModel(
        get(),
        get(),
        get(),
        get(),
        get(),
    ) } //ReminderViewModel
}
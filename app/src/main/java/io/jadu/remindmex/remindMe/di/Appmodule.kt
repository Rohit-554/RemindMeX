package io.jadu.remindmex.remindMe.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import io.jadu.remindmex.remindMe.presentation.viewModels.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    viewModel { LoginViewModel() }

/*    single<GoogleSignInClient> {
        val context: Context = androidContext()
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // get from google-services.json
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }*/

    /*viewModel {
        LoginViewModel(
            firebaseAuth = get(),
            googleSignInClient = get()
        )
    }*/

}
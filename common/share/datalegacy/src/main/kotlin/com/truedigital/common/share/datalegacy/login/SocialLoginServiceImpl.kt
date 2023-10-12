package com.truedigital.common.share.datalegacy.login

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.truedigital.authentication.social.SocialLoginCallbacks
import com.truedigital.authentication.social.SocialLoginService
import com.truedigital.authentication.social.model.SocialAuthParams
import com.truedigital.common.share.datalegacy.R
import com.truedigital.core.provider.ContextDataProvider
import javax.inject.Inject

class SocialLoginServiceImpl @Inject constructor(
    private val contextDataProvider: ContextDataProvider
) : SocialLoginService {

    companion object {
        private const val SOCIAL_LOGIN_REQUEST_CODE = 999
        private const val SOCIAL_LOGIN_PROVIDER_GOOGLE = "google"
    }

    private lateinit var params: SocialAuthParams
    private lateinit var callbacks: SocialLoginCallbacks

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SOCIAL_LOGIN_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleGoogleSignInResult(task)
                }
                else -> {} // Do nothing
            }
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            completedTask.getResult(ApiException::class.java)?.let { account ->
                callbacks.onSuccess(params.provider, account.idToken)
            }
        } catch (error: ApiException) {
            callbacks.onError(params.provider, error.statusCode.toString())
        }
    }

    override fun socialAuth(
        activity: Activity,
        params: SocialAuthParams,
        callbacks: SocialLoginCallbacks
    ) {
        this.params = params
        this.callbacks = callbacks

        if (params.provider == SOCIAL_LOGIN_PROVIDER_GOOGLE) {
            val googleSignInClient = provideGoogleSignInClient(activity)
            activity.startActivityForResult(
                googleSignInClient.signInIntent,
                SOCIAL_LOGIN_REQUEST_CODE
            )
        }
    }

    fun provideGoogleSignInClient(activity: Activity): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.server_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun provideGoogleSignInClient(): GoogleSignInClient {
        lateinit var activity: Activity
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                contextDataProvider.getDataContext().getString(R.string.server_client_id)
            )
            .requestEmail()
            .requestProfile()
            .build()

        contextDataProvider.getDataActivity()?.apply {
            activity = this
        }

        return GoogleSignIn.getClient(activity, gso)
    }
}

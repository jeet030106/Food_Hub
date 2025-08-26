package com.example.foodhub_app.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.foodhub_app.GoogleServiceClientId
import com.example.foodhub_app.data.model.GoogleAccount
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthUiProvider {
    suspend fun signIn(
        activityContext: Context,
        credentialManager: CredentialManager
        ): GoogleAccount{
        val creds=credentialManager.getCredential(
            activityContext,
            getCredentialRequest()
        ).credential
        return handleCredentials(creds)
    }

    private fun handleCredentials(creds: Credential): GoogleAccount {
        when{
            creds is CustomCredential && creds.type== GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL->{
                val dataBundle=creds.data
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(dataBundle)
                Log.d("GoogleAuthUiProvider", "handleCredentials: ${googleIdTokenCredential.idToken}")
                return GoogleAccount(
                    token=googleIdTokenCredential.idToken,
                    displayName = googleIdTokenCredential.displayName?:"",
                    profileImageUrl=googleIdTokenCredential.profilePictureUri.toString()
                )
            }
            else->{
                throw IllegalArgumentException("Invalid credentials")
            }
        }
    }


    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(
                GetSignInWithGoogleOption.Builder(
                    GoogleServiceClientId
            ).build()
            ).build()


    }
}
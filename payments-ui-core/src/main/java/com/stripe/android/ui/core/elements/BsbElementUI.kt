package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.stripe.android.uicore.elements.IdentifierSpec
import com.stripe.android.uicore.elements.Section
import com.stripe.android.uicore.elements.TextField
import com.stripe.android.uicore.stripeColors

@Composable
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun BsbElementUI(
    enabled: Boolean,
    element: BsbElement,
    lastTextFieldIdentifier: IdentifierSpec?
) {
    val error by element.textElement.controller.error.collectAsState()
    val bankName by element.bankName.collectAsState()
    val sectionErrorString = error?.let {
        it.formatArgs?.let { args ->
            stringResource(
                it.errorMessage,
                *args
            )
        } ?: stringResource(it.errorMessage)
    }
    Column {
        Section(
            null,
            sectionErrorString,
            contentInCard = {
                TextField(
                    element.textElement.controller,
                    enabled = enabled,
                    imeAction = if (lastTextFieldIdentifier == element.identifier) {
                        ImeAction.Done
                    } else {
                        ImeAction.Next
                    }
                )
            },
            contentOutsideCard = {
                bankName?.let {
                    Text(
                        it,
                        color = MaterialTheme.stripeColors.subtitle
                    )
                }
            }
        )
    }
}

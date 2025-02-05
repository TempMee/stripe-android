package com.stripe.android.paymentsheet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stripe.android.screenshottesting.PaparazziRule
import com.stripe.android.ui.core.R
import org.junit.Rule
import org.junit.Test

internal class PaymentMethodRowButtonScreenshotTest {

    @get:Rule
    val paparazziRule = PaparazziRule(
        boxModifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )

    @Test
    fun testInitialState() {
        paparazziRule.snapshot {
            PaymentMethodRowButton(
                isEnabled = true,
                isSelected = false,
                iconContent = {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_paymentsheet_pm_card),
                        contentDescription = null
                    )
                },
                textContent = { Text(text = "**** 4242") },
                onClick = {},
            )
        }
    }

    @Test
    fun testDisabledState() {
        paparazziRule.snapshot {
            PaymentMethodRowButton(
                isEnabled = false,
                isSelected = false,
                iconContent = {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_paymentsheet_pm_card),
                        contentDescription = null
                    )
                },
                textContent = { Text(text = "**** 4242") },
                onClick = {},
            )
        }
    }

    @Test
    fun testSelectedState() {
        paparazziRule.snapshot {
            PaymentMethodRowButton(
                isEnabled = true,
                isSelected = true,
                iconContent = {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_paymentsheet_pm_card),
                        contentDescription = null
                    )
                },
                textContent = { Text(text = "**** 4242") },
                onClick = {},
            )
        }
    }

    @Test
    fun testMultilineText() {
        paparazziRule.snapshot {
            PaymentMethodRowButton(
                isEnabled = true,
                isSelected = false,
                iconContent = {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_paymentsheet_pm_card),
                        contentDescription = null
                    )
                },
                textContent = {
                    Column {
                        Text(text = "**** 4242")
                        Text(text = "Please click me, I'm fancy")
                    }
                },
                onClick = {},
            )
        }
    }

    @Test
    fun testMultilineTextTruncation() {
        paparazziRule.snapshot {
            PaymentMethodRowButton(
                isEnabled = true,
                isSelected = false,
                iconContent = {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_paymentsheet_pm_card),
                        contentDescription = null
                    )
                },
                textContent = {
                    Column {
                        Text(text = "**** 4242")
                        Text(text = "Please click me, I'm fancy, but I shouldn't extend a a a a forever.")
                    }
                },
                onClick = {},
            )
        }
    }

    @Test
    fun testTailingContent() {
        paparazziRule.snapshot {
            PaymentMethodRowButton(
                isEnabled = true,
                isSelected = false,
                iconContent = {
                    Image(
                        painter = painterResource(id = R.drawable.stripe_ic_paymentsheet_pm_card),
                        contentDescription = null
                    )
                },
                textContent = {
                    Column {
                        Text(text = "**** 4242")
                    }
                },
                onClick = {},
                trailingContent = {
                    Text(text = "View more")
                }
            )
        }
    }
}

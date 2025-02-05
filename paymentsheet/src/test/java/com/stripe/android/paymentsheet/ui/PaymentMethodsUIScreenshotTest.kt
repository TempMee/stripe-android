package com.stripe.android.paymentsheet.ui

import androidx.compose.foundation.lazy.LazyListState
import com.stripe.android.lpmfoundations.luxe.SupportedPaymentMethod
import com.stripe.android.lpmfoundations.paymentmethod.definitions.ExternalPaymentMethodUiDefinitionFactory
import com.stripe.android.model.PaymentMethodFixtures
import com.stripe.android.screenshottesting.FontSize
import com.stripe.android.screenshottesting.PaparazziRule
import com.stripe.android.screenshottesting.SystemAppearance
import com.stripe.android.ui.core.R
import com.stripe.android.utils.MockPaymentMethodsFactory
import com.stripe.android.utils.screenshots.PaymentSheetAppearance
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class PaymentMethodsUIScreenshotTest {

    @get:Rule
    val paparazziRule = PaparazziRule(
        listOf(SystemAppearance.LightTheme),
        listOf(PaymentSheetAppearance.DefaultAppearance),
        listOf(FontSize.LargeFont),
    )

    private val paymentMethods: List<SupportedPaymentMethod> by lazy {
        MockPaymentMethodsFactory.create()
    }

    @Test
    fun testInitialState() {
        paparazziRule.snapshot {
            PaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testLongPaymentMethodName() {
        val bankPaymentMethod = MockPaymentMethodsFactory.mockPaymentMethod(
            code = "us_bank_account",
            displayNameResource = R.string.stripe_paymentsheet_payment_method_us_bank_account,
            iconResource = R.drawable.stripe_ic_paymentsheet_pm_bank,
            iconRequiresTinting = true
        )
        val paymentMethods = paymentMethods.toMutableList()
        paymentMethods.add(1, bankPaymentMethod)
        paparazziRule.snapshot {
            PaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testScrolledToEnd() {
        paparazziRule.snapshot {
            PaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 3,
                isEnabled = true,
                state = LazyListState(firstVisibleItemIndex = 3),
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testTwoPaymentMethodsExpandToFit() {
        val paymentMethods = paymentMethods.take(2)
        paparazziRule.snapshot {
            PaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testExternalPaymentMethod_iconUrlFailsToLoad() {
        val paymentMethods = listOf(
            ExternalPaymentMethodUiDefinitionFactory(
                PaymentMethodFixtures.PAYPAL_EXTERNAL_PAYMENT_METHOD_SPEC
            ).createSupportedPaymentMethod()
        ).plus(paymentMethods)
        paparazziRule.snapshot {
            PaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testInvalidIconUrlAndInvalidResource() {
        val paymentMethods = listOf(
            SupportedPaymentMethod(
                code = "example_pm",
                displayNameResource = R.string.stripe_paymentsheet_payment_method_affirm,
                iconResource = 0,
                lightThemeIconUrl = null,
                darkThemeIconUrl = null,
                iconRequiresTinting = false,
            )
        ).plus(paymentMethods)
        paparazziRule.snapshot {
            PaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testInitialStateForVerticalMode() {
        paparazziRule.snapshot {
            VerticalModePaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testLongPaymentMethodNameForVerticalMode() {
        val bankPaymentMethod = MockPaymentMethodsFactory.mockPaymentMethod(
            code = "us_bank_account",
            displayNameResource = R.string.stripe_paymentsheet_payment_method_us_bank_account,
            iconResource = R.drawable.stripe_ic_paymentsheet_pm_bank,
            iconRequiresTinting = true
        )
        val paymentMethods = paymentMethods.toMutableList()
        paymentMethods.add(1, bankPaymentMethod)
        paparazziRule.snapshot {
            VerticalModePaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testTwoPaymentMethodsExpandToFitForVerticalMode() {
        val paymentMethods = paymentMethods.take(2)
        paparazziRule.snapshot {
            VerticalModePaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testExternalPaymentMethod_iconUrlFailsToLoadForVerticalMode() {
        val paymentMethods = listOf(
            ExternalPaymentMethodUiDefinitionFactory(
                PaymentMethodFixtures.PAYPAL_EXTERNAL_PAYMENT_METHOD_SPEC
            ).createSupportedPaymentMethod()
        ).plus(paymentMethods)
        paparazziRule.snapshot {
            VerticalModePaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }

    @Test
    fun testInvalidIconUrlAndInvalidResourceForVerticalMode() {
        val paymentMethods = listOf(
            SupportedPaymentMethod(
                code = "example_pm",
                displayNameResource = R.string.stripe_paymentsheet_payment_method_affirm,
                iconResource = 0,
                lightThemeIconUrl = null,
                darkThemeIconUrl = null,
                iconRequiresTinting = false,
            )
        ).plus(paymentMethods)
        paparazziRule.snapshot {
            VerticalModePaymentMethodsUI(
                paymentMethods = paymentMethods,
                selectedIndex = 0,
                isEnabled = true,
                onItemSelectedListener = {},
                imageLoader = mock(),
            )
        }
    }
}

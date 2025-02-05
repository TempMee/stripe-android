package com.stripe.android.model.parsers

import com.google.common.truth.Truth.assertThat
import com.stripe.android.core.model.parsers.ModelJsonParser
import com.stripe.android.model.CardBrand
import com.stripe.android.model.DeferredIntentParams
import com.stripe.android.model.ElementsSession
import com.stripe.android.model.ElementsSessionFixtures
import com.stripe.android.model.ElementsSessionParams
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.SetupIntent
import com.stripe.android.model.StripeIntent
import org.json.JSONObject
import org.junit.Test

class ElementsSessionJsonParserTest {
    @Test
    fun parsePaymentIntent_shouldCreateObjectWithOrderedPaymentMethods() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON
        )
        val orderedPaymentMethods =
            ModelJsonParser.jsonArrayToList(
                ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON
                    .optJSONObject("payment_method_preference")!!
                    .optJSONArray("ordered_payment_method_types")
            )

        assertThat(elementsSession?.stripeIntent?.id)
            .isEqualTo("pi_3JTDhYIyGgrkZxL71IDUGKps")
        assertThat(elementsSession?.stripeIntent?.paymentMethodTypes)
            .containsExactlyElementsIn(orderedPaymentMethods)
            .inOrder()
        assertThat(elementsSession?.linkSettings?.linkPassthroughModeEnabled).isFalse()
    }

    @Test
    fun parseSetupIntent_shouldCreateObjectWithOrderedPaymentMethods() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.SetupIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_SETUP_INTENT_JSON
        )
        val orderedPaymentMethods =
            ModelJsonParser.jsonArrayToList(
                ElementsSessionFixtures.EXPANDED_SETUP_INTENT_JSON
                    .optJSONObject("payment_method_preference")!!
                    .optJSONArray("ordered_payment_method_types")
            )

        assertThat(elementsSession?.stripeIntent?.id)
            .isEqualTo("seti_1JTDqGIyGgrkZxL7reCXkpr5")
        assertThat(elementsSession?.stripeIntent?.paymentMethodTypes)
            .containsExactlyElementsIn(orderedPaymentMethods)
            .inOrder()
    }

    @Test
    fun parsePaymentIntent_shouldCreateObjectLinkFundingSources() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_WITH_LINK_FUNDING_SOURCES_JSON
        )!!

        assertThat(elementsSession.stripeIntent.linkFundingSources)
            .containsExactly("card", "bank_account")
        assertThat(elementsSession.linkSettings?.linkPassthroughModeEnabled).isTrue()
    }

    @Test
    fun parsePaymentIntent_shouldCreateMapLinkFlags() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_WITH_LINK_FUNDING_SOURCES_JSON
        )!!

        assertThat(elementsSession.linkSettings?.linkFlags).containsExactlyEntriesIn(
            mapOf(
                "link_authenticated_change_event_enabled" to false,
                "link_bank_incentives_enabled" to false,
                "link_bank_onboarding_enabled" to false,
                "link_email_verification_login_enabled" to false,
                "link_financial_incentives_experiment_enabled" to false,
                "link_local_storage_login_enabled" to true,
                "link_only_for_payment_method_types_enabled" to false,
                "link_passthrough_mode_enabled" to true,
            )
        )
        assertThat(elementsSession.linkSettings?.linkPassthroughModeEnabled).isTrue()
    }

    @Test
    fun parsePaymentIntent_shouldDisableLinkSignUp() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_SETUP_INTENT_WITH_LINK_SIGNUP_DISABLED_JSON
        )!!

        assertThat(elementsSession.linkSettings?.disableLinkSignup).isTrue()
    }

    @Test
    fun parsePaymentIntent_shouldSetDisableLinkSignUpToFalse() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_SETUP_INTENT_WITH_LINK_SIGNUP_DISABLED_FLAG_FALSE_JSON
        )!!

        assertThat(elementsSession.linkSettings?.disableLinkSignup).isFalse()
    }

    @Test
    fun parseSetupIntent_shouldCreateObjectLinkFundingSources() {
        val elementsSession = ElementsSessionJsonParser(
            ElementsSessionParams.SetupIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_SETUP_INTENT_WITH_LINK_FUNDING_SOURCES_JSON
        )!!

        assertThat(elementsSession.stripeIntent.linkFundingSources)
            .containsExactly("card", "bank_account")
        assertThat(elementsSession.linkSettings?.linkPassthroughModeEnabled).isFalse()
    }

    @Test
    fun `Test ordered payment methods returned in PI payment_method_type variable`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            JSONObject(
                ElementsSessionFixtures.PI_WITH_CARD_AFTERPAY_AU_BECS
            )
        )
        assertThat(parsedData?.stripeIntent?.paymentMethodTypes).isEqualTo(
            listOf(
                "au_becs_debit",
                "afterpay_clearpay",
                "card"
            )
        )
    }

    @Test
    fun `Test ordered payment methods not required in response`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            JSONObject(
                ElementsSessionFixtures.PI_WITH_CARD_AFTERPAY_AU_BECS_NO_ORDERED_LPMS
            )
        )
        // This is the order in the original payment intent
        assertThat(parsedData?.stripeIntent?.paymentMethodTypes).isEqualTo(
            listOf(
                "card",
                "afterpay_clearpay",
                "au_becs_debit"
            )
        )
    }

    @Test
    fun `Test ordered payment methods is not required`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            JSONObject(
                """
                    {
                      "payment_method_preference": {
                         "object": "payment_method_preference",
                         "payment_intent": {
                         }
                      }
                    }
                """.trimIndent()
            )
        )
        assertThat(parsedData?.stripeIntent).isNull()
    }

    @Test
    fun `Test fail to parse the payment intent`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            JSONObject(
                """
                    {
                      "payment_method_preference": {
                         "object": "payment_method_preference",
                         "payment_intent": {
                         }
                      }
                    }
                """.trimIndent()
            )
        )
        assertThat(parsedData?.stripeIntent).isNull()
    }

    @Test
    fun `Test fail to find the payment intent`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            JSONObject(
                """
                    {
                      "payment_method_preference": {
                         "object": "payment_method_preference"
                      }
                    }
                """.trimIndent()
            )
        )
        assertThat(parsedData?.stripeIntent).isNull()
    }

    @Test
    fun `Test PI with country code`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON
        )

        val countryCode = when (val intent = parsedData?.stripeIntent) {
            is PaymentIntent -> intent.countryCode
            is SetupIntent -> intent.countryCode
            null -> null
        }

        assertThat(countryCode).isEqualTo("US")
    }

    @Test
    fun `Test SI with country code`() {
        val parsedData = ElementsSessionJsonParser(
            ElementsSessionParams.SetupIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        ).parse(
            ElementsSessionFixtures.EXPANDED_SETUP_INTENT_JSON
        )

        val countryCode = when (val intent = parsedData?.stripeIntent) {
            is PaymentIntent -> intent.countryCode
            is SetupIntent -> intent.countryCode
            null -> null
        }

        assertThat(countryCode).isEqualTo("US")
    }

    @Test
    fun `Test deferred PaymentIntent`() {
        val data = ElementsSessionJsonParser(
            ElementsSessionParams.DeferredIntentType(
                deferredIntentParams = DeferredIntentParams(
                    mode = DeferredIntentParams.Mode.Payment(
                        amount = 2000,
                        currency = "usd",
                        captureMethod = PaymentIntent.CaptureMethod.Automatic,
                        setupFutureUsage = null,
                    ),
                    paymentMethodTypes = emptyList(),
                    paymentMethodConfigurationId = null,
                    onBehalfOf = null,
                ),
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test",
            timeProvider = { 1 }
        ).parse(
            ElementsSessionFixtures.DEFERRED_INTENT_JSON
        )

        val deferredIntent = data?.stripeIntent

        assertThat(deferredIntent).isNotNull()
        assertThat(deferredIntent).isEqualTo(
            PaymentIntent(
                id = "elements_session_1t6ejApXCS5",
                clientSecret = null,
                amount = 2000L,
                currency = "usd",
                captureMethod = PaymentIntent.CaptureMethod.Automatic,
                countryCode = "CA",
                created = 1,
                isLiveMode = false,
                setupFutureUsage = null,
                unactivatedPaymentMethods = listOf(),
                paymentMethodTypes = listOf("card", "link", "cashapp"),
                linkFundingSources = listOf("card")
            )
        )
    }

    @Test
    fun `Test deferred SetupIntent`() {
        val data = ElementsSessionJsonParser(
            ElementsSessionParams.DeferredIntentType(
                deferredIntentParams = DeferredIntentParams(
                    mode = DeferredIntentParams.Mode.Setup(
                        currency = "usd",
                        setupFutureUsage = StripeIntent.Usage.OffSession,
                    ),
                    paymentMethodTypes = emptyList(),
                    paymentMethodConfigurationId = null,
                    onBehalfOf = null,
                ),
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test",
            timeProvider = { 1 }
        ).parse(
            ElementsSessionFixtures.DEFERRED_INTENT_JSON
        )

        val deferredIntent = data?.stripeIntent

        assertThat(deferredIntent).isNotNull()
        assertThat(deferredIntent).isEqualTo(
            SetupIntent(
                id = "elements_session_1t6ejApXCS5",
                clientSecret = null,
                cancellationReason = null,
                description = null,
                nextActionData = null,
                paymentMethodId = null,
                paymentMethod = null,
                status = null,
                countryCode = "CA",
                created = 1,
                isLiveMode = false,
                usage = StripeIntent.Usage.OffSession,
                unactivatedPaymentMethods = listOf(),
                paymentMethodTypes = listOf("card", "link", "cashapp"),
                linkFundingSources = listOf("card")
            )
        )
    }

    @Test
    fun `Is eligible for CBC if response says so`() {
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test",
        )

        val intent = ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON_WITH_CBC_ELIGIBLE
        val session = parser.parse(intent)

        assertThat(session?.isEligibleForCardBrandChoice).isTrue()
    }

    @Test
    fun `Is not eligible for CBC if response says so`() {
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test",
        )

        val intent = ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON_WITH_CBC_NOT_ELIGIBLE
        val session = parser.parse(intent)

        assertThat(session?.isEligibleForCardBrandChoice).isFalse()
    }

    @Test
    fun `Is not eligible for CBC if no info in the response`() {
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test",
        )

        val intent = ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON
        val session = parser.parse(intent)

        assertThat(session?.isEligibleForCardBrandChoice).isFalse()
    }

    @Test
    fun `ElementsSesssion has no external payment methods when they are not included in response`() {
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = listOf("external_venmo"),
            ),
            apiKey = "test",
        )

        val intent = ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON
        val session = parser.parse(intent)

        assertThat(session?.externalPaymentMethodData).isNull()
    }

    @Test
    fun `ElementsSession has external payment methods when they are included in response`() {
        val venmo = "external_venmo"
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = listOf(venmo),
            ),
            apiKey = "test",
        )

        val intent = ElementsSessionFixtures.PAYMENT_INTENT_WITH_EXTERNAL_VENMO_JSON
        val session = parser.parse(intent)

        assertThat(session?.externalPaymentMethodData).contains(venmo)
    }

    @Test
    fun `has customer session information in the response`() {
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                customerSessionClientSecret = "customer_session_client_secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test",
        )

        val intent = ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_WITH_CUSTOMER_SESSION
        val elementsSession = parser.parse(intent)

        assertThat(elementsSession?.customer).isEqualTo(
            ElementsSession.Customer(
                session = ElementsSession.Customer.Session(
                    id = "cuss_123",
                    apiKey = "ek_test_1234",
                    apiKeyExpiry = 1713890664,
                    customerId = "cus_1",
                    liveMode = false,
                ),
                defaultPaymentMethod = "pm_123",
                paymentMethods = listOf(
                    PaymentMethod(
                        id = "pm_123",
                        customerId = "cus_1",
                        type = PaymentMethod.Type.Card,
                        code = "card",
                        created = 1550757934255,
                        liveMode = false,
                        billingDetails = null,
                        card = PaymentMethod.Card(
                            brand = CardBrand.Visa,
                            last4 = "4242",
                            expiryMonth = 8,
                            expiryYear = 2032,
                            country = "US",
                            funding = "credit",
                            fingerprint = "fingerprint123",
                            checks = PaymentMethod.Card.Checks(
                                addressLine1Check = "unchecked",
                                cvcCheck = "unchecked",
                                addressPostalCodeCheck = null,
                            ),
                            threeDSecureUsage = PaymentMethod.Card.ThreeDSecureUsage(
                                isSupported = true
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun parsePaymentIntent_shouldCreateObjectWithCorrectGooglePayEnabled() {
        val parser = ElementsSessionJsonParser(
            ElementsSessionParams.PaymentIntentType(
                clientSecret = "secret",
                externalPaymentMethods = emptyList(),
            ),
            apiKey = "test"
        )

        fun assertIsGooglePayEnabled(expectedValue: Boolean, jsonTransform: JSONObject.() -> Unit) {
            val json = ElementsSessionFixtures.EXPANDED_PAYMENT_INTENT_JSON
            json.jsonTransform()
            assertThat(parser.parse(json)?.isGooglePayEnabled).isEqualTo(expectedValue)
        }

        assertIsGooglePayEnabled(true) { remove(ElementsSessionJsonParser.FIELD_GOOGLE_PAY_PREFERENCE) }
        assertIsGooglePayEnabled(true) { put(ElementsSessionJsonParser.FIELD_GOOGLE_PAY_PREFERENCE, "enabled") }
        assertIsGooglePayEnabled(true) { put(ElementsSessionJsonParser.FIELD_GOOGLE_PAY_PREFERENCE, "unknown") }
        assertIsGooglePayEnabled(false) { put(ElementsSessionJsonParser.FIELD_GOOGLE_PAY_PREFERENCE, "disabled") }
    }
}

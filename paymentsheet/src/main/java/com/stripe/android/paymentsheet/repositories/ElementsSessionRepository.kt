package com.stripe.android.paymentsheet.repositories

import com.stripe.android.PaymentConfiguration
import com.stripe.android.core.injection.IOContext
import com.stripe.android.core.networking.ApiRequest
import com.stripe.android.model.ElementsSession
import com.stripe.android.model.ElementsSessionParams
import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.SetupIntent
import com.stripe.android.model.StripeIntent
import com.stripe.android.networking.StripeRepository
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.toDeferredIntentParams
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.CoroutineContext

internal interface ElementsSessionRepository {
    suspend fun get(
        initializationMode: PaymentSheet.InitializationMode,
        customer: PaymentSheet.CustomerConfiguration?,
        externalPaymentMethods: List<String>,
    ): Result<ElementsSession>
}

/**
 * Retrieve the [StripeIntent] from the [StripeRepository].
 */
internal class RealElementsSessionRepository @Inject constructor(
    private val stripeRepository: StripeRepository,
    private val lazyPaymentConfig: Provider<PaymentConfiguration>,
    @IOContext private val workContext: CoroutineContext,
) : ElementsSessionRepository {

    // The PaymentConfiguration can change after initialization, so this needs to get a new
    // request options each time requested.
    private val requestOptions: ApiRequest.Options
        get() = ApiRequest.Options(
            apiKey = lazyPaymentConfig.get().publishableKey,
            stripeAccount = lazyPaymentConfig.get().stripeAccountId,
        )

    override suspend fun get(
        initializationMode: PaymentSheet.InitializationMode,
        customer: PaymentSheet.CustomerConfiguration?,
        externalPaymentMethods: List<String>,
    ): Result<ElementsSession> {
        val params = initializationMode.toElementsSessionParams(
            customer = customer,
            externalPaymentMethods = externalPaymentMethods
        )

        val elementsSession = stripeRepository.retrieveElementsSession(
            params = params,
            options = requestOptions,
        )

        return elementsSession.getResultOrElse { elementsSessionFailure ->
            fallback(params, elementsSessionFailure)
        }
    }

    private suspend fun fallback(
        params: ElementsSessionParams,
        elementsSessionFailure: Throwable,
    ): Result<ElementsSession> = withContext(workContext) {
        when (params) {
            is ElementsSessionParams.PaymentIntentType -> {
                stripeRepository.retrievePaymentIntent(
                    clientSecret = params.clientSecret,
                    options = requestOptions,
                    expandFields = listOf("payment_method")
                ).map { intent ->
                    ElementsSession.createFromFallback(
                        stripeIntent = intent.withoutWeChatPay(),
                        sessionsError = elementsSessionFailure,
                    )
                }
            }
            is ElementsSessionParams.SetupIntentType -> {
                stripeRepository.retrieveSetupIntent(
                    clientSecret = params.clientSecret,
                    options = requestOptions,
                    expandFields = listOf("payment_method")
                ).map { intent ->
                    ElementsSession.createFromFallback(
                        stripeIntent = intent.withoutWeChatPay(),
                        sessionsError = elementsSessionFailure,
                    )
                }
            }
            is ElementsSessionParams.DeferredIntentType -> {
                // We don't have a fallback endpoint for the deferred intent flow
                Result.failure(elementsSessionFailure)
            }
        }
    }
}

private fun StripeIntent.withoutWeChatPay(): StripeIntent {
    // We don't know if the merchant is eligible for H5 payments, so we filter out WeChat Pay.
    val filteredPaymentMethodTypes = paymentMethodTypes.filter { it != PaymentMethod.Type.WeChatPay.code }
    return when (this) {
        is PaymentIntent -> copy(paymentMethodTypes = filteredPaymentMethodTypes)
        is SetupIntent -> copy(paymentMethodTypes = filteredPaymentMethodTypes)
    }
}

internal fun PaymentSheet.InitializationMode.toElementsSessionParams(
    customer: PaymentSheet.CustomerConfiguration?,
    externalPaymentMethods: List<String>,
): ElementsSessionParams {
    val customerSessionClientSecret = customer?.toElementSessionParam()

    return when (this) {
        is PaymentSheet.InitializationMode.PaymentIntent -> {
            ElementsSessionParams.PaymentIntentType(
                clientSecret = clientSecret,
                customerSessionClientSecret = customerSessionClientSecret,
                externalPaymentMethods = externalPaymentMethods,
            )
        }

        is PaymentSheet.InitializationMode.SetupIntent -> {
            ElementsSessionParams.SetupIntentType(
                clientSecret = clientSecret,
                customerSessionClientSecret = customerSessionClientSecret,
                externalPaymentMethods = externalPaymentMethods,
            )
        }

        is PaymentSheet.InitializationMode.DeferredIntent -> {
            ElementsSessionParams.DeferredIntentType(
                deferredIntentParams = intentConfiguration.toDeferredIntentParams(),
                externalPaymentMethods = externalPaymentMethods,
                customerSessionClientSecret = customerSessionClientSecret,
            )
        }
    }
}

private fun PaymentSheet.CustomerConfiguration.toElementSessionParam(): String? {
    return when (accessType) {
        is PaymentSheet.CustomerAccessType.CustomerSession -> accessType.customerSessionClientSecret
        is PaymentSheet.CustomerAccessType.LegacyCustomerEphemeralKey -> null
    }
}

private inline fun <T> Result<T>.getResultOrElse(
    transform: (Throwable) -> Result<T>,
): Result<T> {
    return exceptionOrNull()?.let(transform) ?: this
}

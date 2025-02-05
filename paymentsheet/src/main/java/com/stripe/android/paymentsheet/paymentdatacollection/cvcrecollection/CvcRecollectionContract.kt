package com.stripe.android.paymentsheet.paymentdatacollection.cvcrecollection

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import com.stripe.android.model.CardBrand
import com.stripe.android.paymentsheet.PaymentSheet
import kotlinx.parcelize.Parcelize

internal class CvcRecollectionContract :
    ActivityResultContract<CvcRecollectionContract.Args, CvcRecollectionResult>() {

    override fun createIntent(context: Context, input: Args): Intent {
        return Intent(context, CvcRecollectionActivity::class.java)
            .putExtra(EXTRA_ARGS, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CvcRecollectionResult {
        return CvcRecollectionResult.fromIntent(intent)
    }

    @Parcelize
    data class Args(
        val lastFour: String,
        val cardBrand: CardBrand,
        val appearance: PaymentSheet.Appearance
    ) : Parcelable {
        companion object {
            fun fromIntent(intent: Intent): Args? {
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(EXTRA_ARGS, Args::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra(EXTRA_ARGS)
                }
            }
        }
    }

    internal companion object {
        internal const val EXTRA_ARGS: String = "extra_activity_args"
    }
}

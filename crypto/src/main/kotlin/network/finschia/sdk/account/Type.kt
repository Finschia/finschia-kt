package network.finschia.sdk.account

/**
 * Defines the types of Addresses and PubKeys
 */
enum class Type {
    /**
     * Account
     */
    ACCOUNT,

    /**
     * Validator Consensus
     */
    VALIDATOR_CONSENSUS,

    /**
     * Validator Operator
     */
    VALIDATOR_OPERATOR;

    companion object {
        internal const val DEFAULT_BECH32_HRP_PREFIX = "link"

        private const val ACCOUNT_HRP_PREFIX = ""
        private const val VALIDATOR_CONSENSUS_HRP_PREFIX = "valcons"
        private const val VALIDATOR_OPERATOR_HRP_PREFIX = "valoper"

        /**
         * Gets the type from the [hrpTypePrefix]
         */
        operator fun invoke(hrpTypePrefix: String): Type {

            return when (hrpTypePrefix) {
                ACCOUNT_HRP_PREFIX -> ACCOUNT
                VALIDATOR_CONSENSUS_HRP_PREFIX -> VALIDATOR_CONSENSUS
                VALIDATOR_OPERATOR_HRP_PREFIX -> VALIDATOR_OPERATOR
                else -> throw IllegalArgumentException("Illegal HRP Type ('$hrpTypePrefix' is not supported)")
            }
        }

        /**
         * Converts to HRP type prefix
         */
        fun Type.toHrpPrefix(): String {
            return when (this) {
                ACCOUNT -> ACCOUNT_HRP_PREFIX
                VALIDATOR_CONSENSUS -> VALIDATOR_CONSENSUS_HRP_PREFIX
                VALIDATOR_OPERATOR -> VALIDATOR_OPERATOR_HRP_PREFIX
            }
        }
    }

}

package co.uk.thewirelessguy.moovtech.util

/**
 * State Management for UI & Data.
 */
sealed class State<T> {
    class Loading<T>() : State<T>()

    data class Success<T>(val data: T) : State<T>()

    data class Error<T>(val message: String) : State<T>()

    companion object {

        /**
         * Returns [State.Loading] instance.
         * @param data Data to emit with status.
         */
        fun <T> loading(data: T?) = Loading<T>()

        /**
         * Returns [State.Success] instance.
         * @param data Data to emit with status.
         */
        fun <T> success(data: T) =
            Success(data)

        /**
         * Returns [State.Error] instance.
         * @param message Description of failure.
         * @param data Data to emit with status.
         */
        fun <T> error(message: String, data: T?) =
            Error<T>(message)
    }
}
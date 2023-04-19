package example.app.sofaweatherapp.repository

import example.app.sofaweatherapp.model.Result
import retrofit2.Response

abstract class BasicRepository {

    protected suspend fun <T : Any> apiCall(call: suspend () -> Response<T>): Result<T> {
        val response: Response<T>
        try {
            response = call.invoke()
        } catch (t: Throwable) {
            return Result.Error(Exception(t))
        }

        return if (!response.isSuccessful) {
            @Suppress("BlockingMethodInNonBlockingContext")
            Result.Error(Exception(response.message()))
        } else {
            return if (response.body() == null) {
                Result.Error(Exception("response.body() can't be null"))
            } else {
                Result.Success(response.body()!!)
            }
        }
    }
}

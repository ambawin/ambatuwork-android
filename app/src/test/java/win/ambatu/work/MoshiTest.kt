package win.ambatu.work

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import win.ambatu.work.feature.network.GoogleAuthRequest

class MoshiTest {
    @Test
    fun testGoogleAuthRequestSerialization() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(GoogleAuthRequest::class.java)
        
        val request = GoogleAuthRequest(idToken = "test_token", deviceName = "test_device")
        val json = adapter.toJson(request)
        
        // If it works correctly, it should contain "id_token" and "device_name"
        println("Serialized JSON: $json")
        assert(json.contains("\"id_token\":\"test_token\""))
        assert(json.contains("\"device_name\":\"test_device\""))
    }
}

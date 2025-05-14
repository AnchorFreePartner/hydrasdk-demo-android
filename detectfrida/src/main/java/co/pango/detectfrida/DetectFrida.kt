package co.pango.detectfrida

class DetectFrida {
    init {
        System.loadLibrary("detect-frida")
    }

    fun detect(fridaDetectedListener: FridaDetectListener) {
        initializeNative(fridaDetectedListener)
    }
}
fun interface FridaDetectListener {
    fun onDetected()
}
external fun initializeNative(fridaDetectedListener: FridaDetectListener)
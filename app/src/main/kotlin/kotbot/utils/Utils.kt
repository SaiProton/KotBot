package kotbot.utils

// checks if filename is an image
fun checkIfImage(filename: String): Boolean =
    filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg")
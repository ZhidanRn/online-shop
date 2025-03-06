package com.example.onlineshop.ui.screen.user

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.onlineshop.R
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.model.User
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.data.repository.UserProfileRepository
import com.example.onlineshop.ui.component.EditProfileDialog
import com.example.onlineshop.ui.component.ProfileInfoItem
import com.example.onlineshop.ui.component.ProfilePictureDialog
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = cacheDir
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    authRepository: AuthRepository,
    userRepository: UserProfileRepository,
    dataStoreManager: DataStoreManager,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showProfilePictureDialog by remember { mutableStateOf(false) }
    var userData by remember { mutableStateOf<User?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    var cameraPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraPhotoUri != null) {
            selectedImageUri = cameraPhotoUri
            coroutineScope.launch {
                try {
                    userRepository.updateProfilePicture(cameraPhotoUri!!)
                    userData = userData?.copy(profileImageUrl = cameraPhotoUri.toString())
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = context.createImageFile()
            cameraPhotoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            cameraLauncher.launch(cameraPhotoUri)
        } else {
            Toast.makeText(context, "Camera permission is required to take a photo", Toast.LENGTH_SHORT).show()
        }
    }

    // Copy image to local storage
    fun Context.copyImageToLocalStorage(sourceUri: Uri): Uri? {
        return try {
            val destinationFile = createImageFile()

            contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                destinationFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                destinationFile
            )
        } catch (e: Exception) {
            null
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val localUri = context.copyImageToLocalStorage(uri)
            if (localUri != null) {
                selectedImageUri = localUri
                coroutineScope.launch {
                    try {
                        userRepository.updateProfilePicture(localUri)
                        userData = userData?.copy(profileImageUrl = localUri.toString())
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Failed to process the image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            userData = userRepository.getCurrentUserData()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture & Name
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Show selected image or default profile picture
                        val imagePainter = if (selectedImageUri != null) {
                            rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build()
                            )
                        } else if (userData?.profileImageUrl != null) {
                            rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(userData?.profileImageUrl)
                                    .crossfade(true)
                                    .build()
                            )
                        } else {
                            painterResource(id = R.drawable.profile_placeholder)
                        }

                        Image(
                            painter = imagePainter,
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(CircleShape)
                                .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { showProfilePictureDialog = true },
                            modifier = Modifier
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit Foto Profil", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userData?.name ?: "Loading...",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Personal Information Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Personal Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Informasi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                    ProfileInfoItem(Icons.Default.Email, "Email", userData?.email ?: "Loading...")
                    ProfileInfoItem(Icons.Default.Phone, "Nomor Telepon", userData?.phone ?: "Loading...")
                    ProfileInfoItem(Icons.Default.Place, "Alamat", userData?.address ?: "Loading...")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Keluar", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Edit Profile Information Dialog
    if (showEditDialog) {
        EditProfileDialog(
            user = userData,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                coroutineScope.launch {
                    try {
                        userRepository.updateUserProfile(updatedUser)
                        userData = userRepository.getCurrentUserData() // Refresh data
                    } catch (e: Exception) {
                        // Handle error
                    }
                }
                showEditDialog = false
            }
        )
    }

    // Edit Profile Picture Dialog
    if (showProfilePictureDialog) {
        ProfilePictureDialog(
            onDismiss = { showProfilePictureDialog = false },
            onGallery = {
                galleryLauncher.launch("image/*")
                showProfilePictureDialog = false
            },
            onCamera = {
                // Request camera permission and launch camera
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                showProfilePictureDialog = false
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmation") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        authRepository.logout(dataStoreManager)
                        onLogout()
                    }
                    showLogoutDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}
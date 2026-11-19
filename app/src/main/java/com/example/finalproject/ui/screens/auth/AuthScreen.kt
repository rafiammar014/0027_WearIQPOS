package com.example.finalproject.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.finalproject.ui.components.AmbientBackground
import com.example.finalproject.ui.theme.CrimsonRed
import com.example.finalproject.ui.theme.DarkNavyBackground
import com.example.finalproject.ui.theme.DeepRedContainer
import com.example.finalproject.ui.theme.ErrorBrightRed
import com.example.finalproject.ui.theme.WarmCream

/**
 * AuthScreen WearIQ POS:
 * 1. Root memiliki background solid Dark Navy (#0B132B) untuk mencegah ghosting/overlap transisi.
 * 2. Seluruh konten dibungkus dalam Column dengan verticalScroll, statusBarsPadding, navigationBarsPadding, dan imePadding.
 * 3. Spacer(28.dp) di bawah form agar tombol tidak tertutup navigation bar atau keyboard.
 * 4. Spacing antar field 12.dp proporsional.
 * 5. State loading: Semua input, tab, dan tombol demo nonaktif (enabled = false, alpha 0.5f) dan tombol aksi memakai warna #B3E63946 (Red 70%).
 */
@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.provideFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Masuk, 1 = Daftar Baru

    // Login Form State
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regFullName by remember { mutableStateOf("") }
    var regUsername by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("KASIR") } // "KASIR" | "OWNER"

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    val isLoading = uiState.isLoading

    // Root berlatar solid Dark Navy (#0B132B) mencegah overlap animasi
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        AmbientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                // ================= 1. HEADER (LEFT-ALIGNED) =================
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(WarmCream.copy(alpha = 0.08f))
                        .border(1.dp, WarmCream.copy(alpha = 0.16f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Storefront,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "FASHION POS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarmCream,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "WearIQ POS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmCream,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Kelola kasir & stok butik lebih cerdas.",
                    fontSize = 14.sp,
                    color = WarmCream.copy(alpha = 0.65f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // ================= 2. MODE SWITCHER (GLASS SEGMENTED PILL) =================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(WarmCream.copy(alpha = 0.08f))
                        .border(1.dp, WarmCream.copy(alpha = 0.15f), CircleShape)
                        .padding(4.dp)
                        .alpha(if (isLoading) 0.5f else 1f)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        SegmentedPillTab(
                            text = "Masuk",
                            isSelected = selectedTab == 0,
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedTab = 0
                                viewModel.clearError()
                            }
                        )
                        SegmentedPillTab(
                            text = "Daftar Baru",
                            isSelected = selectedTab == 1,
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedTab = 1
                                viewModel.clearError()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ================= 3. ERROR BANNER =================
                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    uiState.errorMessage?.let { errorText ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DeepRedContainer.copy(alpha = 0.70f))
                                .border(1.dp, ErrorBrightRed.copy(alpha = 0.50f), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = errorText,
                                color = WarmCream,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // ================= 4. FORM CONTENT =================
                if (selectedTab == 0) {
                    // ---------- TAB: MASUK ----------
                    GlassOutlinedTextField(
                        value = loginEmail,
                        onValueChange = { loginEmail = it },
                        label = "Email atau Username",
                        placeholder = "owner@weariq.com",
                        leadingIcon = Icons.Rounded.Email,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    GlassOutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = "Kata Sandi",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Rounded.Lock,
                        enabled = !isLoading,
                        trailingIcon = {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible },
                                enabled = !isLoading
                            ) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                                    contentDescription = null,
                                    tint = WarmCream.copy(alpha = 0.65f)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (loginEmail.isNotBlank() && loginPassword.isNotBlank()) {
                                    viewModel.login(loginEmail.trim(), loginPassword)
                                }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tombol Aksi Utama (CTA)
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.login(loginEmail.trim(), loginPassword)
                        },
                        enabled = !isLoading && loginEmail.isNotBlank() && loginPassword.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonRed,
                            contentColor = WarmCream,
                            disabledContainerColor = if (isLoading) Color(0xB3E63946) else WarmCream.copy(alpha = 0.08f),
                            disabledContentColor = if (isLoading) WarmCream else WarmCream.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = WarmCream,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Menghubungkan...",
                                color = WarmCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Masuk ke Sistem",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = WarmCream
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = WarmCream
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // ---------- AKSES CEPAT DEMO ----------
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isLoading) 0.5f else 1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = WarmCream.copy(alpha = 0.12f)
                        )
                        Text(
                            text = "Atau coba cepat",
                            fontSize = 12.sp,
                            color = WarmCream.copy(alpha = 0.50f),
                            fontWeight = FontWeight.Medium
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = WarmCream.copy(alpha = 0.12f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isLoading) 0.5f else 1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickDemoButton(
                            title = "Demo Owner",
                            icon = Icons.Rounded.AdminPanelSettings,
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                loginEmail = "owner@weariq.com"
                                loginPassword = "owner123"
                                viewModel.clearError()
                            }
                        )
                        QuickDemoButton(
                            title = "Demo Kasir",
                            icon = Icons.Rounded.PointOfSale,
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                loginEmail = "kasir@weariq.com"
                                loginPassword = "kasir123"
                                viewModel.clearError()
                            }
                        )
                    }

                } else {
                    // ---------- TAB: DAFTAR BARU ----------
                    GlassOutlinedTextField(
                        value = regFullName,
                        onValueChange = { regFullName = it },
                        label = "Nama Lengkap",
                        placeholder = "Ahmad Dani",
                        leadingIcon = Icons.Rounded.Person,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassOutlinedTextField(
                        value = regUsername,
                        onValueChange = { regUsername = it },
                        label = "Username",
                        placeholder = "ahmad_pos",
                        leadingIcon = Icons.Rounded.Badge,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassOutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = "Email",
                        placeholder = "ahmad@weariq.com",
                        leadingIcon = Icons.Rounded.Email,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassOutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = "Kata Sandi",
                        placeholder = "Minimal 6 karakter",
                        leadingIcon = Icons.Rounded.Lock,
                        enabled = !isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pemilihan Peran Akun (Dua Kartu Sejajar)
                    Text(
                        text = "Pilih Peran Akun",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = WarmCream.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isLoading) 0.5f else 1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RoleSelectionCard(
                            title = "Kasir",
                            subtitle = "Akses kasir POS & cetak struk",
                            icon = Icons.Rounded.PointOfSale,
                            isSelected = regRole == "KASIR",
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            onClick = { regRole = "KASIR" }
                        )
                        RoleSelectionCard(
                            title = "Owner",
                            subtitle = "Akses omzet & AI analytics",
                            icon = Icons.Rounded.AdminPanelSettings,
                            isSelected = regRole == "OWNER",
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f),
                            onClick = { regRole = "OWNER" }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.register(
                                fullName = regFullName.trim(),
                                username = regUsername.trim(),
                                email = regEmail.trim(),
                                password = regPassword,
                                role = regRole
                            )
                        },
                        enabled = !isLoading && regFullName.isNotBlank() && regUsername.isNotBlank() && regEmail.isNotBlank() && regPassword.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonRed,
                            contentColor = WarmCream,
                            disabledContainerColor = if (isLoading) Color(0xB3E63946) else WarmCream.copy(alpha = 0.08f),
                            disabledContentColor = if (isLoading) WarmCream else WarmCream.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = WarmCream,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Mendaftarkan...",
                                color = WarmCream,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Daftarkan Akun",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = WarmCream
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = WarmCream
                                )
                            }
                        }
                    }
                }

                // Spacer 28.dp di bawah tombol form agar tidak pernah terpotong navigation bar atau keyboard
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun SegmentedPillTab(
    text: String,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(if (isSelected) WarmCream else Color.Transparent)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) DarkNavyBackground else WarmCream.copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun GlassOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        enabled = enabled,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = WarmCream.copy(alpha = if (enabled) 0.65f else 0.35f),
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = WarmCream.copy(alpha = 0.08f),
            unfocusedContainerColor = WarmCream.copy(alpha = 0.05f),
            disabledContainerColor = WarmCream.copy(alpha = 0.03f),
            focusedBorderColor = WarmCream.copy(alpha = 0.60f),
            unfocusedBorderColor = WarmCream.copy(alpha = 0.12f),
            disabledBorderColor = WarmCream.copy(alpha = 0.06f),
            focusedLabelColor = WarmCream,
            unfocusedLabelColor = WarmCream.copy(alpha = 0.65f),
            disabledLabelColor = WarmCream.copy(alpha = 0.35f),
            focusedTextColor = WarmCream,
            unfocusedTextColor = WarmCream,
            disabledTextColor = WarmCream.copy(alpha = 0.45f),
            focusedPlaceholderColor = WarmCream.copy(alpha = 0.35f),
            unfocusedPlaceholderColor = WarmCream.copy(alpha = 0.35f),
            cursorColor = CrimsonRed
        ),
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.5f)
    )
}

@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) CrimsonRed.copy(alpha = 0.15f) else WarmCream.copy(alpha = 0.05f)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) CrimsonRed else WarmCream.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) CrimsonRed.copy(alpha = 0.25f) else WarmCream.copy(alpha = 0.08f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) CrimsonRed else WarmCream.copy(alpha = 0.70f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) WarmCream else WarmCream.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = WarmCream.copy(alpha = 0.50f),
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun QuickDemoButton(
    title: String,
    icon: ImageVector,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(WarmCream.copy(alpha = 0.05f))
            .border(1.dp, WarmCream.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WarmCream.copy(alpha = if (enabled) 0.75f else 0.4f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = WarmCream.copy(alpha = if (enabled) 0.85f else 0.4f)
            )
        }
    }
}

package org.jansaarthi.app.ui.screens.eligibility

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.data.model.ALL_INDIAN_STATES_AND_UTS
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.components.AccessibilityBar
import org.jansaarthi.app.ui.components.GovernmentBanner
import org.jansaarthi.app.ui.components.JanSaarthiPrimaryButton
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.*

/**
 * JanSaarthi Citizen Eligibility Profile Screen UI (Frontend Only)
 * Temporary local UI state only; no database, backend, or Firebase code.
 */
data class EligibilityProfileUiData(
    val age: String = "28",
    val dob: String = "",
    val gender: String = "Female",
    val areaType: String = "Rural",
    val state: String = "Maharashtra",
    val district: String = "Pune",
    val socialCategory: String = "OBC",
    val isMinority: Boolean = false,
    val minorityCommunity: String = "Muslim",
    val hasDisability: Boolean = false,
    val disabilityType: String = "Locomotor Disability (40%+)",
    val isStudent: Boolean = false,
    val educationLevel: String = "Undergraduate",
    val employmentStatus: String = "Self-Employed / Business",
    val isFarmer: Boolean = true,
    val farmerCategory: String = "Small & Marginal (< 2 Hectares)",
    val annualIncomeRange: String = "₹1,00,000 - ₹2,50,000"
)

// Common District mappings for quick selection in major states
val SAMPLE_DISTRICTS_BY_STATE: Map<String, List<String>> = mapOf(
    "Maharashtra" to listOf("Pune", "Mumbai City", "Mumbai Suburban", "Nagpur", "Nashik", "Thane", "Aurangabad", "Solapur", "Kolhapur", "Amravati"),
    "Uttar Pradesh" to listOf("Lucknow", "Varanasi", "Kanpur Nagar", "Prayagraj", "Agra", "Noida (GB Nagar)", "Gorakhpur", "Meerut", "Bareilly", "Aligarh"),
    "Bihar" to listOf("Patna", "Gaya", "Muzaffarpur", "Bhagalpur", "Darbhanga", "Purnia", "Rohtas", "Samastipur"),
    "Madhya Pradesh" to listOf("Bhopal", "Indore", "Jabalpur", "Gwalior", "Ujjain", "Sagar", "Rewa"),
    "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Erode"),
    "Karnataka" to listOf("Bengaluru Urban", "Mysuru", "Hubballi-Dharwad", "Mangaluru", "Belagavi", "Kalaburagi"),
    "Gujarat" to listOf("Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar", "Jamnagar", "Gandhinagar"),
    "Rajasthan" to listOf("Jaipur", "Jodhpur", "Kota", "Udaipur", "Bikaner", "Ajmer", "Bhilwara"),
    "West Bengal" to listOf("Kolkata", "Howrah", "North 24 Parganas", "South 24 Parganas", "Hooghly", "Darjeeling"),
    "Kerala" to listOf("Thiruvananthapuram", "Kochi (Ernakulam)", "Kozhikode", "Thrissur", "Kollam", "Kannur"),
    "Delhi" to listOf("New Delhi", "Central Delhi", "South Delhi", "North Delhi", "East Delhi", "West Delhi"),
    "Punjab" to listOf("Amritsar", "Ludhiana", "Jalandhar", "Patiala", "Bathinda", "Mohali (SAS Nagar)"),
    "Haryana" to listOf("Gurugram", "Faridabad", "Ambala", "Hisar", "Panipat", "Karnal", "Rohtak")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EligibilityProfileScreen(
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onSelectLanguage: (AppLanguage) -> Unit = {},
    isLargeFont: Boolean = false,
    onFontScaleToggle: () -> Unit = {},
    initialStateName: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    onContinue: (EligibilityProfileUiData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isHindi = currentLanguage == AppLanguage.HINDI
    val fontSizeMultiplier = if (isLargeFont) 1.15f else 1.0f

    // -------------------------------------------------------------
    // TEMPORARY LOCAL/MOCK STATE ONLY (No backend or Firebase)
    // -------------------------------------------------------------
    var ageInput by remember { mutableStateOf("28") }
    var dobInput by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Female") }
    var selectedAreaType by remember { mutableStateOf("Rural") } // Rural, Urban

    var selectedState by remember { mutableStateOf(initialStateName ?: "Maharashtra") }
    var stateDropdownExpanded by remember { mutableStateOf(false) }

    var selectedDistrict by remember { mutableStateOf("Pune") }
    var districtDropdownExpanded by remember { mutableStateOf(false) }

    var selectedSocialCategory by remember { mutableStateOf("OBC") } // General, OBC, SC, ST, EWS
    var isMinority by remember { mutableStateOf(false) }
    var selectedMinorityCommunity by remember { mutableStateOf("Muslim") }
    var minorityDropdownExpanded by remember { mutableStateOf(false) }

    var hasDisability by remember { mutableStateOf(false) }
    var selectedDisabilityType by remember { mutableStateOf("Locomotor Disability (40%+)") }
    var disabilityDropdownExpanded by remember { mutableStateOf(false) }

    var isStudent by remember { mutableStateOf(false) }
    var selectedEducationLevel by remember { mutableStateOf("Undergraduate") }
    var educationDropdownExpanded by remember { mutableStateOf(false) }

    var selectedEmploymentStatus by remember { mutableStateOf("Self-Employed / Business") }
    var employmentDropdownExpanded by remember { mutableStateOf(false) }

    var isFarmer by remember { mutableStateOf(true) }
    var selectedFarmerCategory by remember { mutableStateOf("Small & Marginal (< 2 Hectares)") }
    var farmerDropdownExpanded by remember { mutableStateOf(false) }

    var selectedIncomeRange by remember { mutableStateOf("₹1,00,000 - ₹2,50,000") }
    var incomeDropdownExpanded by remember { mutableStateOf(false) }

    // Dynamic districts list according to selected state
    val availableDistricts = remember(selectedState) {
        SAMPLE_DISTRICTS_BY_STATE[selectedState] ?: listOf("District Headquarters", "Central District", "Rural Sub-Division", "Urban Block")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovBackground)
    ) {
        // Government Tricolor Banner
        GovernmentBanner(language = currentLanguage, fontSizeMultiplier = fontSizeMultiplier)

        // Main Scrollable Form Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Accessibility controls (1-tap language switch & font resizer)
            AccessibilityBar(
                currentLanguage = currentLanguage,
                onSelectLanguage = onSelectLanguage,
                isLargeFont = isLargeFont,
                onFontScaleToggle = onFontScaleToggle
            )

            // Top Navigation row (if back navigation is provided)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onNavigateBack != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateBack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "वापस जाएं" else "Back",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = GovNavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    Surface(
                        color = GovNavyContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isHindi) "चरण 2 • पात्रता प्रोफाइल" else "Step 2 of 2 • Eligibility Profile",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GovNavyPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }
                }

                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF166534),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "सुरक्षित प्रोफाइल" else "Confidential",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF166534),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Screen Header & Introduction
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (isHindi) "नागरिक पात्रता प्रोफाइल" else "Eligibility Profile",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (22 * fontSizeMultiplier).sp
                    )
                )
                Text(
                    text = if (isHindi)
                        "अपनी बुनियादी जनसांख्यिकीय व सामाजिक जानकारी भरें ताकि आपको केवल वही सरकारी योजनाएं और लाभ दिखें जिनके आप पात्र हैं।"
                    else
                        "Complete your basic socio-economic profile to discover verified Central and State welfare schemes tailored for you.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF475569),
                        fontSize = (13 * fontSizeMultiplier).sp,
                        lineHeight = 18.sp
                    )
                )
            }

            // DPDP Protection Notice Badge
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isHindi)
                            "यह जानकारी केवल योजना पात्रता जांचने हेतु है। कोई संवेदनशील दस्तावेज (आधार/बैंक) नहीं लिया जा रहा है।"
                        else
                            "This data is used solely to match eligibility. No sensitive bank/Aadhaar documents are collected.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF14532D),
                            fontSize = (11 * fontSizeMultiplier).sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // =========================================================
            // SECTION 1: Personal & Demographics (DOB/Age, Gender, Area)
            // =========================================================
            ProfileSectionCard(
                title = if (isHindi) "1. व्यक्तिगत विवरण (Personal Details)" else "1. Personal & Demographics",
                icon = Icons.Default.Person,
                fontSizeMultiplier = fontSizeMultiplier
            ) {
                // Age / Date of Birth
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { if (it.length <= 3 && it.all { ch -> ch.isDigit() }) ageInput = it },
                        modifier = Modifier.weight(1f),
                        label = { Text(if (isHindi) "आयु (वर्ष) *" else "Age (Years) *") },
                        placeholder = { Text("उदा. 28") },
                        leadingIcon = {
                            Icon(Icons.Default.Cake, contentDescription = null, tint = GovNavyPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = dobInput,
                        onValueChange = { dobInput = it },
                        modifier = Modifier.weight(1.3f),
                        label = { Text(if (isHindi) "जन्मतिथि (वैकल्पिक)" else "DOB (Optional)") },
                        placeholder = { Text("DD/MM/YYYY") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF64748B))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Gender (Radio buttons / Selectable Chips)
                Text(
                    text = if (isHindi) "लिंग (Gender) *" else "Gender *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val genders = listOf(
                        Pair("Female", if (isHindi) "महिला (Female)" else "Female"),
                        Pair("Male", if (isHindi) "पुरुष (Male)" else "Male"),
                        Pair("Other", if (isHindi) "अन्य (Other)" else "Other")
                    )

                    genders.forEach { (key, label) ->
                        val isSelected = selectedGender == key
                        Surface(
                            onClick = { selectedGender = key },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GovNavyContainer else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GovNavyPrimary else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 6.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedGender = key },
                                    colors = RadioButtonDefaults.colors(selectedColor = GovNavyPrimary),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) GovNavyPrimary else GovTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = (11 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Rural / Urban Area (Radio / Segmented Switch)
                Text(
                    text = if (isHindi) "निवास क्षेत्र (Area of Residence) *" else "Area of Residence (Rural / Urban) *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val areas = listOf(
                        Pair("Rural", if (isHindi) "🌾 ग्रामीण (Rural Area)" else "🌾 Rural (Village/Gramin)"),
                        Pair("Urban", if (isHindi) "🏙️ शहरी (Urban Area)" else "🏙️ Urban (City/Town)")
                    )

                    areas.forEach { (key, label) ->
                        val isSelected = selectedAreaType == key
                        Surface(
                            onClick = { selectedAreaType = key },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFD97706) else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 8.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAreaType = key },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFD97706)),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color(0xFF92400E) else GovTextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = (12 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // SECTION 2: Location & Residence (State, District)
            // =========================================================
            ProfileSectionCard(
                title = if (isHindi) "2. निवास स्थान (Location & Residence)" else "2. Location & Residence",
                icon = Icons.Default.LocationOn,
                fontSizeMultiplier = fontSizeMultiplier
            ) {
                // State Dropdown
                ExposedDropdownMenuBox(
                    expanded = stateDropdownExpanded,
                    onExpandedChange = { stateDropdownExpanded = !stateDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isHindi) "गृह राज्य / यूटी *" else "Resident State / UT *") },
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GovNavyPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = stateDropdownExpanded,
                        onDismissRequest = { stateDropdownExpanded = false }
                    ) {
                        ALL_INDIAN_STATES_AND_UTS.forEach { stateItem ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${stateItem.nameEn} (${stateItem.nameHi})",
                                        fontWeight = if (stateItem.nameEn == selectedState) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedState = stateItem.nameEn
                                    stateDropdownExpanded = false
                                    // Reset district to first available in state
                                    val districts = SAMPLE_DISTRICTS_BY_STATE[stateItem.nameEn]
                                    selectedDistrict = districts?.firstOrNull() ?: "District Center"
                                }
                            )
                        }
                    }
                }

                // District Dropdown
                ExposedDropdownMenuBox(
                    expanded = districtDropdownExpanded,
                    onExpandedChange = { districtDropdownExpanded = !districtDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isHindi) "जिला (District) *" else "District *") },
                        leadingIcon = {
                            Icon(Icons.Default.PinDrop, contentDescription = null, tint = GovSaffron)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = districtDropdownExpanded,
                        onDismissRequest = { districtDropdownExpanded = false }
                    ) {
                        availableDistricts.forEach { dist ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = dist,
                                        fontWeight = if (dist == selectedDistrict) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedDistrict = dist
                                    districtDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // =========================================================
            // SECTION 3: Social Category & Minority Status
            // =========================================================
            ProfileSectionCard(
                title = if (isHindi) "3. सामाजिक श्रेणी व अल्पसंख्यक दर्जा" else "3. Social Category & Minority Status",
                icon = Icons.Default.Groups,
                fontSizeMultiplier = fontSizeMultiplier
            ) {
                Text(
                    text = if (isHindi) "सामाजिक श्रेणी (Social Category) *" else "Social Category *",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary
                    )
                )

                // Radio buttons / Flow chips for Social Category
                val categories = listOf("General", "OBC", "SC", "ST", "EWS")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedSocialCategory == cat
                        Surface(
                            onClick = { selectedSocialCategory = cat },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GovNavyPrimary else Color(0xFFF1F5F9),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) GovNavyPrimary else Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White else GovTextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (12 * fontSizeMultiplier).sp
                                    )
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Minority Status Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "अल्पसंख्यक समुदाय दर्जा" else "Minority Community Status",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = GovTextPrimary
                            )
                        )
                        Text(
                            text = if (isHindi)
                                "मुस्लिम, ईसाई, सिख, बौद्ध, जैन अथवा पारसी समुदाय"
                            else
                                "Notified religious minorities (Muslim, Christian, Sikh, Buddhist, Jain, Parsi)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }

                    Switch(
                        checked = isMinority,
                        onCheckedChange = { isMinority = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GovNavyPrimary)
                    )
                }

                // If Minority is enabled: Dropdown to pick community
                if (isMinority) {
                    val minorityCommunities = listOf("Muslim", "Christian", "Sikh", "Buddhist", "Jain", "Parsi")
                    ExposedDropdownMenuBox(
                        expanded = minorityDropdownExpanded,
                        onExpandedChange = { minorityDropdownExpanded = !minorityDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMinorityCommunity,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isHindi) "अल्पसंख्यक समुदाय चुनें" else "Select Minority Community") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minorityDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = minorityDropdownExpanded,
                            onDismissRequest = { minorityDropdownExpanded = false }
                        ) {
                            minorityCommunities.forEach { comm ->
                                DropdownMenuItem(
                                    text = { Text(comm) },
                                    onClick = {
                                        selectedMinorityCommunity = comm
                                        minorityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // SECTION 4: Occupation, Farmer & Student Status
            // =========================================================
            ProfileSectionCard(
                title = if (isHindi) "4. आजीविका, किसान एवं छात्र स्थिति" else "4. Occupation, Farmer & Student Status",
                icon = Icons.Default.Work,
                fontSizeMultiplier = fontSizeMultiplier
            ) {
                // Employment Status Dropdown
                val employmentOptions = listOf(
                    "Unemployed",
                    "Salaried (Private Sector)",
                    "Government Employee",
                    "Self-Employed / Business",
                    "Daily Wage / Construction / Gig Worker",
                    "Homemaker"
                )

                ExposedDropdownMenuBox(
                    expanded = employmentDropdownExpanded,
                    onExpandedChange = { employmentDropdownExpanded = !employmentDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedEmploymentStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isHindi) "रोजगार की स्थिति *" else "Employment Status *") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = GovNavyPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = employmentDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = employmentDropdownExpanded,
                        onDismissRequest = { employmentDropdownExpanded = false }
                    ) {
                        employmentOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    selectedEmploymentStatus = opt
                                    employmentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Farmer Status Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "क्या आप किसान या कृषि श्रमिक हैं?" else "Farmer / Agricultural Status",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = GovTextPrimary
                            )
                        )
                        Text(
                            text = if (isHindi)
                                "PM-Kisan एवं कृषि सब्सिडी योजनाओं के मिलान हेतु"
                            else
                                "Enables PM-KISAN, crop insurance and agri-machinery subsidies",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }

                    Switch(
                        checked = isFarmer,
                        onCheckedChange = { isFarmer = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GovNavyPrimary)
                    )
                }

                // If Farmer: Landholding Category
                if (isFarmer) {
                    val farmerCategories = listOf(
                        "Small & Marginal (< 2 Hectares)",
                        "Medium Landholding (2 to 10 Hectares)",
                        "Large Landholding (> 10 Hectares)",
                        "Landless Agricultural Labourer",
                        "Tenant Farmer / Sharecropper"
                    )

                    ExposedDropdownMenuBox(
                        expanded = farmerDropdownExpanded,
                        onExpandedChange = { farmerDropdownExpanded = !farmerDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedFarmerCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isHindi) "कृषि भूमि श्रेणी" else "Landholding Category") },
                            leadingIcon = {
                                Icon(Icons.Default.Agriculture, contentDescription = null, tint = GovGreen)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = farmerDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = farmerDropdownExpanded,
                            onDismissRequest = { farmerDropdownExpanded = false }
                        ) {
                            farmerCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedFarmerCategory = cat
                                        farmerDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Student Status Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "क्या आप वर्तमान में छात्र हैं?" else "Student Status (Enrolled Student)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = GovTextPrimary
                            )
                        )
                        Text(
                            text = if (isHindi)
                                "छात्रवृत्ति व शिक्षा ऋण योजनाओं हेतु"
                            else
                                "Enables National Scholarships and education grants",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }

                    Switch(
                        checked = isStudent,
                        onCheckedChange = { isStudent = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GovNavyPrimary)
                    )
                }

                // If Student: Education Level Dropdown
                if (isStudent) {
                    val educationLevels = listOf(
                        "School (Class 1 to 10)",
                        "Higher Secondary (Class 11 - 12)",
                        "Vocational / ITI / Diploma",
                        "Undergraduate (BA / BSc / BTech / MBBS / etc.)",
                        "Postgraduate (MA / MSc / MTech / MBA)",
                        "Doctorate / Research Scholar"
                    )

                    ExposedDropdownMenuBox(
                        expanded = educationDropdownExpanded,
                        onExpandedChange = { educationDropdownExpanded = !educationDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedEducationLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isHindi) "शिक्षा स्तर (Education Level)" else "Education Level") },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null, tint = GovNavyPrimary)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = educationDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = educationDropdownExpanded,
                            onDismissRequest = { educationDropdownExpanded = false }
                        ) {
                            educationLevels.forEach { lvl ->
                                DropdownMenuItem(
                                    text = { Text(lvl) },
                                    onClick = {
                                        selectedEducationLevel = lvl
                                        educationDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // SECTION 5: Economic Income & Disability Status
            // =========================================================
            ProfileSectionCard(
                title = if (isHindi) "5. आर्थिक आय व दिव्यांगजन स्थिति" else "5. Annual Income & Disability Status",
                icon = Icons.Default.CurrencyRupee,
                fontSizeMultiplier = fontSizeMultiplier
            ) {
                // Annual Family Income Dropdown
                val incomeRanges = listOf(
                    "Below ₹1,00,000 (BPL / Antyodaya)",
                    "₹1,00,000 - ₹2,50,000",
                    "₹2,50,000 - ₹5,00,000",
                    "₹5,00,000 - ₹8,00,000 (Non-Creamy / EWS Limit)",
                    "Above ₹8,00,000"
                )

                ExposedDropdownMenuBox(
                    expanded = incomeDropdownExpanded,
                    onExpandedChange = { incomeDropdownExpanded = !incomeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedIncomeRange,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isHindi) "वार्षिक पारिवारिक आय *" else "Annual Family Income *") },
                        leadingIcon = {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GovNavyPrimary)
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = incomeDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = incomeDropdownExpanded,
                        onDismissRequest = { incomeDropdownExpanded = false }
                    ) {
                        incomeRanges.forEach { range ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = range,
                                        fontWeight = if (range == selectedIncomeRange) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedIncomeRange = range
                                    incomeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Disability Status Switch (Divyangjan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "दिव्यांगजन स्थिति (Person with Disability - PwD)" else "Disability Status (Divyangjan / PwD)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = GovTextPrimary
                            )
                        )
                        Text(
                            text = if (isHindi)
                                "दिव्यांगजन पेंशन व सहायक उपकरण योजनाओं हेतु"
                            else
                                "Enables assistive aid schemes and Divyangjan pensions",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF64748B),
                                fontSize = (11 * fontSizeMultiplier).sp
                            )
                        )
                    }

                    Switch(
                        checked = hasDisability,
                        onCheckedChange = { hasDisability = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GovNavyPrimary)
                    )
                }

                // If Disability: Type dropdown
                if (hasDisability) {
                    val disabilityTypes = listOf(
                        "Locomotor Disability (40%+)",
                        "Visual Impairment (Blindness / Low Vision)",
                        "Hearing / Speech Impairment",
                        "Intellectual / Learning Disability",
                        "Multiple Disabilities"
                    )

                    ExposedDropdownMenuBox(
                        expanded = disabilityDropdownExpanded,
                        onExpandedChange = { disabilityDropdownExpanded = !disabilityDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDisabilityType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isHindi) "दिव्यांगता प्रकार" else "Disability Category") },
                            leadingIcon = {
                                Icon(Icons.Default.Accessible, contentDescription = null, tint = GovSaffron)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disabilityDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = disabilityDropdownExpanded,
                            onDismissRequest = { disabilityDropdownExpanded = false }
                        ) {
                            disabilityTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        selectedDisabilityType = type
                                        disabilityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // ACTION BUTTONS (Continue & Skip Options)
            // =========================================================
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    JanSaarthiPrimaryButton(
                        text = if (isHindi) "पात्र योजनाएं देखें (आगे बढ़ें)" else "Continue to Applicable Schemes",
                        leadingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = {
                            val profileResult = EligibilityProfileUiData(
                                age = ageInput.ifBlank { "28" },
                                dob = dobInput,
                                gender = selectedGender,
                                areaType = selectedAreaType,
                                state = selectedState,
                                district = selectedDistrict,
                                socialCategory = selectedSocialCategory,
                                isMinority = isMinority,
                                minorityCommunity = if (isMinority) selectedMinorityCommunity else "None",
                                hasDisability = hasDisability,
                                disabilityType = if (hasDisability) selectedDisabilityType else "None",
                                isStudent = isStudent,
                                educationLevel = if (isStudent) selectedEducationLevel else "None",
                                employmentStatus = selectedEmploymentStatus,
                                isFarmer = isFarmer,
                                farmerCategory = if (isFarmer) selectedFarmerCategory else "None",
                                annualIncomeRange = selectedIncomeRange
                            )
                            onContinue(profileResult)
                        },
                        fontSizeMultiplier = fontSizeMultiplier
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = {
                                onContinue(EligibilityProfileUiData(state = selectedState, district = selectedDistrict))
                            }
                        ) {
                            Text(
                                text = if (isHindi) "बाद में भरें • सीधे मुख्य पृष्ठ पर जाएं" else "Skip for Now • View All Schemes",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Reusable Card container for Profile sections
 */
@Composable
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    fontSizeMultiplier: Float,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = GovNavyContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = GovNavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary,
                        fontSize = (15 * fontSizeMultiplier).sp
                    )
                )
            }

            HorizontalDivider(color = Color(0xFFF1F5F9))

            content()
        }
    }
}

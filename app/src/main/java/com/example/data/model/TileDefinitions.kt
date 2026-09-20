package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class TileCategory(val displayName: String, val badgeColor: Color) {
  WILDLIFE("Wildlife", Color(0xFFE65100)),
  LANDMARKS("Landmarks", Color(0xFF0284C7)),
  MUSIC("Music & Instruments", Color(0xFF9333EA)),
  ART("Art & Textiles", Color(0xFFD97706)),
  FOOD("Food & Culture", Color(0xFF059669))
}

data class TileDefinition(
  val id: String,
  val category: TileCategory,
  val name: String,
  val africanSubtitle: String,
  val symbol: String, // High-contrast visual iconography
  val description: String
)

object TileRegistry {
  val ALL_TILES: List<TileDefinition> = listOf(
    // WILDLIFE
    TileDefinition("lion", TileCategory.WILDLIFE, "Lion", "Simba (Swahili)", "🦁", "King of the African savannah and symbol of royalty."),
    TileDefinition("elephant", TileCategory.WILDLIFE, "Elephant", "Tembo (Swahili)", "🐘", "The African bush elephant, earth's largest land animal."),
    TileDefinition("giraffe", TileCategory.WILDLIFE, "Giraffe", "Twiga (Swahili)", "🦒", "Graceful gentle giant wandering the East African plains."),
    TileDefinition("zebra", TileCategory.WILDLIFE, "Zebra", "Punda Milia", "🦓", "Known for unique black and white striped camouflage."),
    TileDefinition("gorilla", TileCategory.WILDLIFE, "Mtn Gorilla", "Ingagi (Kinyarwanda)", "🦍", "Protected majestic primates of the Virunga mountains."),
    TileDefinition("leopard", TileCategory.WILDLIFE, "Leopard", "Chui (Swahili)", "🐆", "Agile nocturnal hunter revered in African folklore."),
    TileDefinition("rhino", TileCategory.WILDLIFE, "Rhinoceros", "Kifaru (Swahili)", "🦏", "Ancient guardian of African grasslands."),
    TileDefinition("cheetah", TileCategory.WILDLIFE, "Cheetah", "Duma (Swahili)", "🐆", "The fastest land animal on earth."),

    // LANDMARKS
    TileDefinition("zuma_rock", TileCategory.LANDMARKS, "Zuma Rock", "Niger State, NG", "🗿", "Colossal monolithic inselberg rising 725m in Nigeria."),
    TileDefinition("pyramids", TileCategory.LANDMARKS, "Pyramids", "Giza, Egypt", "🏛️", "Ancient wonder constructed with astronomical precision."),
    TileDefinition("kilimanjaro", TileCategory.LANDMARKS, "Kilimanjaro", "Tanzania Peak", "🏔️", "Africa's highest peak crowned in eternal snow."),
    TileDefinition("victoria_falls", TileCategory.LANDMARKS, "Victoria Falls", "Mosi-oa-Tunya", "🌊", "The Smoke That Thunders spanning Zambia and Zimbabwe."),
    TileDefinition("table_mountain", TileCategory.LANDMARKS, "Table Mountain", "Cape Town, SA", "⛰️", "Flat-topped landmark overlooking the Atlantic and Indian oceans."),
    TileDefinition("djenne_mosque", TileCategory.LANDMARKS, "Djenné Mosque", "Great Adobe, ML", "🕌", "Masterpiece of Sudano-Sahelian mud architecture."),

    // MUSIC & INSTRUMENTS
    TileDefinition("djembe", TileCategory.MUSIC, "Djembe Drum", "Goblet Drum", "🪘", "Traditional West African skin-covered goblet drum."),
    TileDefinition("kalimba", TileCategory.MUSIC, "Kalimba / Mbira", "Thumb Piano", "🎵", "Ancient melodic lamellophone from Southern and Central Africa."),
    TileDefinition("kora", TileCategory.MUSIC, "Kora Harp", "21-String Harp", "🪕", "Mandinka harp-lute played by royal oral historians (Griots)."),
    TileDefinition("talking_drum", TileCategory.MUSIC, "Talking Drum", "Gangan / Dondo", "🥁", "Pitch-shifting hourglass drum mimicking tonal human speech."),
    TileDefinition("balafon", TileCategory.MUSIC, "Balafon", "Gourd Xylophone", "🪵", "Wooden xylophone resonant with calabash gourds."),

    // ART & TEXTILES
    TileDefinition("kente", TileCategory.ART, "Kente Cloth", "Ashanti Royal", "🧵", "Woven Ghanaian fabric where every geometric color tells a proverb."),
    TileDefinition("gye_nyame", TileCategory.ART, "Gye Nyame", "Adinkra Symbol", "✨", "Except God: Symbol of supreme divine sovereignty."),
    TileDefinition("cowrie", TileCategory.ART, "Cowrie Shell", "Symbol of Wealth", "🐚", "Ancient currency, divination medium, and ornament of prestige."),
    TileDefinition("bronze_mask", TileCategory.ART, "Benin Bronze", "Queen Idia Mask", "🎭", "Iconic 16th-century brass casting masterpiece of Benin kingdom."),
    TileDefinition("mudcloth", TileCategory.ART, "Bogolanfini", "Malian Mudcloth", "🎨", "Handwoven cotton painted with fermented river mud."),

    // FOOD & HARVEST
    TileDefinition("jollof", TileCategory.FOOD, "Jollof Rice", "West African Pride", "🍛", "Beloved aromatic spiced rice simmered in tomato broth."),
    TileDefinition("fufu", TileCategory.FOOD, "Fufu & Egusi", "Cassava & Melon", "🍲", "Pounded swallow paired with rich melon seed soup."),
    TileDefinition("injera", TileCategory.FOOD, "Injera", "Teff Flatbread", "🫓", "Spongy sourdough flatbread central to Ethiopian dining."),
    TileDefinition("tagine", TileCategory.FOOD, "Tagine", "Maghrebi Clay Pot", "🥘", "North African slow-cooked stew simmered in conical earthenware."),
    TileDefinition("plantain", TileCategory.FOOD, "Plantain", "Dodo / Alloco", "🍌", "Sweet golden fried plantain savored across the continent.")
  )

  private val tileMap = ALL_TILES.associateBy { it.id }

  fun getById(id: String): TileDefinition {
    return tileMap[id] ?: ALL_TILES.first()
  }

  fun getTilesForLevel(levelNumber: Int, requiredPairTypes: Int): List<TileDefinition> {
    // Deterministically rotate available tile sets to match difficulty & variety
    val pool = ALL_TILES.shuffled(java.util.Random(levelNumber.toLong() * 37L))
    return pool.take(requiredPairTypes.coerceIn(4, ALL_TILES.size))
  }
}

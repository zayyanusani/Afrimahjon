package com.example.data.model

data class CountryChapter(
  val id: String,
  val name: String,
  val flag: String,
  val capital: String,
  val region: String,
  val description: String,
  val startLevel: Int,
  val endLevel: Int,
  val unlockStarsRequired: Int,
  val highlightLandmark: String
)

data class CulturalDiscoveryCard(
  val id: String,
  val countryId: String,
  val countryName: String,
  val countryFlag: String,
  val title: String,
  val category: String,
  val subtitle: String,
  val factualDescription: String,
  val unlockLevel: Int,
  val imageDrawableRes: Int? = null
)

object AfricaRegistry {
  val COUNTRIES: List<CountryChapter> = listOf(
    CountryChapter("NG", "Nigeria", "🇳🇬", "Abuja", "West Africa", "Giant of Africa, rich in culture, Afrobeat music, and ancient kingdoms.", 1, 28, 0, "Zuma Rock"),
    CountryChapter("GH", "Ghana", "🇬🇭", "Accra", "West Africa", "Land of Gold, Kente textiles, Ashanti heritage, and Cape Coast.", 29, 56, 40, "Elmina & Cape Coast"),
    CountryChapter("SN", "Senegal", "🇸🇳", "Dakar", "West Africa", "Gateway of Teranga hospitality, Gorée Island, and wrestling traditions.", 57, 84, 85, "Monument of African Renaissance"),
    CountryChapter("KE", "Kenya", "🇰🇪", "Nairobi", "East Africa", "Cradle of humanity, legendary Maasai Mara, and long-distance athletics.", 85, 112, 130, "Maasai Mara National Reserve"),
    CountryChapter("TZ", "Tanzania", "🇹🇿", "Dodoma", "East Africa", "Serengeti endless plains, Mount Kilimanjaro, and spice island Zanzibar.", 113, 140, 180, "Serengeti & Kilimanjaro"),
    CountryChapter("UG", "Uganda", "🇺🇬", "Kampala", "East Africa", "Pearl of Africa, misty mountain gorillas, and source of the Nile River.", 141, 168, 230, "Bwindi Impenetrable Forest"),
    CountryChapter("ET", "Ethiopia", "🇪🇹", "Addis Ababa", "Horn of Africa", "Ancient uncolonized empire, rock-hewn churches of Lalibela, and origin of coffee.", 169, 196, 280, "Lalibela Rock Churches"),
    CountryChapter("EG", "Egypt", "🇪🇬", "Cairo", "North Africa", "Millennia of civilization, Great Pyramids of Giza, and historic Nile valley.", 197, 224, 330, "Pyramids of Giza"),
    CountryChapter("MA", "Morocco", "🇲🇦", "Rabat", "North Africa", "Imperial medinas, Atlas Mountains, Sahara dunes, and aromatic tagines.", 225, 252, 380, "Marrakech Medina & Atlas"),
    CountryChapter("TN", "Tunisia", "🇹🇳", "Tunis", "North Africa", "Ancient Carthage, Mediterranean amphitheater of El Jem, and Matmata.", 253, 280, 430, "Carthage & El Jem"),
    CountryChapter("CM", "Cameroon", "🇨🇲", "Yaoundé", "Central Africa", "Africa in miniature with rainforests, volcanic Mount Cameroon, and diverse peoples.", 281, 308, 480, "Mount Cameroon"),
    CountryChapter("RW", "Rwanda", "🇷🇼", "Kigali", "East/Central Africa", "Land of a Thousand Hills, conservation pioneer, and tranquil Lake Kivu.", 309, 336, 530, "Volcanoes National Park"),
    CountryChapter("CD", "DR Congo", "🇨🇩", "Kinshasa", "Central Africa", "Heart of the Congo Basin rainforest, vibrant rumba music, and Okapi sanctuary.", 337, 364, 580, "Virunga & Congo River"),
    CountryChapter("ZM", "Zambia", "🇿🇲", "Lusaka", "Southern Africa", "Untamed wilderness of South Luangwa and the majestic curtain of Victoria Falls.", 365, 392, 630, "Victoria Falls (Mosi-oa-Tunya)"),
    CountryChapter("ZW", "Zimbabwe", "🇿🇼", "Harare", "Southern Africa", "Great Zimbabwe stone ruins, fertile plateaus, and balancing rocks.", 393, 420, 680, "Great Zimbabwe Ruins"),
    CountryChapter("BW", "Botswana", "🇧🇼", "Gaborone", "Southern Africa", "Pristine jewel of the Okavango Delta and Chobe elephant sanctuaries.", 421, 448, 730, "Okavango Delta"),
    CountryChapter("NA", "Namibia", "🇳🇦", "Windhoek", "Southern Africa", "Towering red dunes of Sossusvlei, Etosha salt pans, and dark sky reserves.", 449, 476, 780, "Sossusvlei Red Dunes"),
    CountryChapter("ZA", "South Africa", "🇿🇦", "Pretoria", "Southern Africa", "Rainbow Nation, Table Mountain, Kruger National Park, and Robben Island.", 477, 500, 830, "Table Mountain & Kruger")
  )

  fun getCountryForLevel(levelNumber: Int): CountryChapter {
    return COUNTRIES.find { levelNumber in it.startLevel..it.endLevel } ?: COUNTRIES.first()
  }

  val CULTURAL_CARDS: List<CulturalDiscoveryCard> = listOf(
    CulturalDiscoveryCard(
      id = "card_zuma_rock",
      countryId = "NG",
      countryName = "Nigeria",
      countryFlag = "🇳🇬",
      title = "Zuma Rock Monolith",
      category = "Landmarks",
      subtitle = "Gateway to Abuja",
      factualDescription = "Zuma Rock is a dramatic inselberg rising 725 meters (2,379 ft) above its surroundings in Niger State, west of Nigeria's capital Abuja. It is depicted on Nigeria's 100 Naira banknote and is historically considered a natural fortress.",
      unlockLevel = 1
    ),
    CulturalDiscoveryCard(
      id = "card_abuja",
      countryId = "NG",
      countryName = "Nigeria",
      countryFlag = "🇳🇬",
      title = "Abuja",
      category = "Cities",
      subtitle = "Federal Capital Territory",
      factualDescription = "Abuja became Nigeria's official capital on December 12, 1991, chosen for its central geographical location, ethnic neutrality, and pleasant climate. It is framed by iconic Aso Rock.",
      unlockLevel = 5
    ),
    CulturalDiscoveryCard(
      id = "card_benin_bronzes",
      countryId = "NG",
      countryName = "Nigeria",
      countryFlag = "🇳🇬",
      title = "Benin Bronzes",
      category = "Art",
      subtitle = "Royal Lost-Wax Metallurgy",
      factualDescription = "Created from the 13th to 19th centuries by master guild metalsmiths of the Kingdom of Benin, these plaques and sculptures are worldwide celebrated examples of complex brass and bronze casting.",
      unlockLevel = 12
    ),
    CulturalDiscoveryCard(
      id = "card_kente_cloth",
      countryId = "GH",
      countryName = "Ghana",
      countryFlag = "🇬🇭",
      title = "Ashanti Kente Cloth",
      category = "Fashion",
      subtitle = "Royal Handwoven Silk & Cotton",
      factualDescription = "Originating among the Ashanti people in Bonwire, Ghana, Kente cloth is woven in narrow strips. Each color carries symbolic philosophy: gold represents royalty and wealth, while green reflects renewal and vegetation.",
      unlockLevel = 30
    ),
    CulturalDiscoveryCard(
      id = "card_gore_island",
      countryId = "SN",
      countryName = "Senegal",
      countryFlag = "🇸🇳",
      title = "Gorée Island & House of Slaves",
      category = "Landmarks",
      subtitle = "UNESCO World Heritage Site",
      factualDescription = "Off the coast of Dakar, Île de Gorée stands as a universal memorial to the transatlantic trade. The 'Door of No Return' represents a solemn monument of global reconciliation and human dignity.",
      unlockLevel = 60
    ),
    CulturalDiscoveryCard(
      id = "card_maasai_mara",
      countryId = "KE",
      countryName = "Kenya",
      countryFlag = "🇰🇪",
      title = "The Great Migration",
      category = "Wildlife",
      subtitle = "Seven Natural Wonders of Africa",
      factualDescription = "Every year, over two million wildebeest, zebras, and gazelles trek across the Serengeti in Tanzania and the Maasai Mara in Kenya in the largest overland mammal migration on planet Earth.",
      unlockLevel = 90
    ),
    CulturalDiscoveryCard(
      id = "card_kilimanjaro",
      countryId = "TZ",
      countryName = "Tanzania",
      countryFlag = "🇹🇿",
      title = "Mount Kilimanjaro",
      category = "Nature",
      subtitle = "Roof of Africa (5,895 m)",
      factualDescription = "Kilimanjaro is the highest free-standing mountain peak in the world. Located in northern Tanzania, its snow-capped summit (Uhuru Peak) supports five distinct ecological zones from rainforest to arctic summit.",
      unlockLevel = 115
    ),
    CulturalDiscoveryCard(
      id = "card_bwindi_gorillas",
      countryId = "UG",
      countryName = "Uganda",
      countryFlag = "🇺🇬",
      title = "Mountain Gorillas of Bwindi",
      category = "Wildlife",
      subtitle = "Bwindi Impenetrable National Park",
      factualDescription = "Uganda's Bwindi Impenetrable Forest is home to roughly half of the world's remaining endangered mountain gorillas, protected through dedicated community conservation programs.",
      unlockLevel = 145
    ),
    CulturalDiscoveryCard(
      id = "card_lalibela",
      countryId = "ET",
      countryName = "Ethiopia",
      countryFlag = "🇪🇹",
      title = "Rock-Hewn Churches of Lalibela",
      category = "Landmarks",
      subtitle = "Monolithic Architectural Marvel",
      factualDescription = "Chiseled out of solid volcanic red tuff rock in the 12th century under King Lalibela, the eleven monolithic churches (including the cruciform Church of St. George) continue as living pilgrim sanctuaries.",
      unlockLevel = 175
    ),
    CulturalDiscoveryCard(
      id = "card_pyramids_giza",
      countryId = "EG",
      countryName = "Egypt",
      countryFlag = "🇪🇬",
      title = "Great Pyramids of Giza",
      category = "Landmarks",
      subtitle = "Sole Standing Ancient Wonder",
      factualDescription = "Constructed over 4,500 years ago during Egypt's Old Kingdom, the Great Pyramid of Khufu was the tallest human-made structure in the world for over 3,800 years.",
      unlockLevel = 200
    ),
    CulturalDiscoveryCard(
      id = "card_marrakech_souks",
      countryId = "MA",
      countryName = "Morocco",
      countryFlag = "🇲🇦",
      title = "Medina of Marrakech",
      category = "Art",
      subtitle = "Historic Walled Imperial City",
      factualDescription = "Founded in 1070 by the Almoravids, the historic medina of Marrakech features vibrant souks, leather tanneries, intricate zellij tilework, and the legendary storytelling square Jemaa el-Fnaa.",
      unlockLevel = 230
    ),
    CulturalDiscoveryCard(
      id = "card_victoria_falls",
      countryId = "ZM",
      countryName = "Zambia / Zimbabwe",
      countryFlag = "🇿🇲",
      title = "Victoria Falls (Mosi-oa-Tunya)",
      category = "Nature",
      subtitle = "The Smoke That Thunders",
      factualDescription = "Spanning 1,708 meters across the Zambezi River with a drop of 108 meters, Victoria Falls forms the largest curtain of falling water on earth. Its spray can be seen from over 30 kilometers away.",
      unlockLevel = 370
    ),
    CulturalDiscoveryCard(
      id = "card_okavango_delta",
      countryId = "BW",
      countryName = "Botswana",
      countryFlag = "🇧🇼",
      title = "Okavango Inland Delta",
      category = "Nature",
      subtitle = "Oasis of the Kalahari",
      factualDescription = "One of the few interior delta systems that does not flow into an ocean, the Okavango Delta floods during the dry season, creating a life-giving sanctuary navigated by traditional wooden mokoro canoes.",
      unlockLevel = 425
    ),
    CulturalDiscoveryCard(
      id = "card_table_mountain",
      countryId = "ZA",
      countryName = "South Africa",
      countryFlag = "🇿🇦",
      title = "Table Mountain & Cape Floral",
      category = "Landmarks",
      subtitle = "Ancient Flat-Topped Sentinel",
      factualDescription = "Table Mountain is one of the oldest mountains in the world (over 200 million years old). Its plateau forms part of the Cape Floral Region, supporting extraordinary fynbos biodiversity found nowhere else on earth.",
      unlockLevel = 480
    )
  )

  fun getCardByLevel(levelNumber: Int): CulturalDiscoveryCard? {
    return CULTURAL_CARDS.find { it.unlockLevel == levelNumber }
  }
}

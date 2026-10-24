package com.example.data

import androidx.compose.ui.graphics.Color

enum class CharacterVisualStyle {
    JESUS_PANTOCRATOR,          // Cruciform gold halo (Ο ΩΝ), crimson chiton, sapphire himation, blessing hand & gospel book
    SIMON_PETER,                // Curly silver beard & hair, ochre-gold mantle, teal tunic, crossed gold & silver keys
    JOHN_BELOVED,               // Youthful beardless face, flowing chestnut hair, emerald tunic, ruby cloak, chalice & scroll
    JAMES_GREATER,              // Full dark beard, pilgrim cloak, staff & scallop emblem
    ANDREW_APOSTLE,             // Flowing silver-gray beard, sea-blue robes, X-saltire cross & Galilean net
    MATTHEW_EVANGELIST,         // Neat dark beard, rich burgundy & gold robes, quill pen & open Gospel codex
    THOMAS_APOSTLE,             // Earnest bearded expression, terracotta mantle, builder's spear & radiant sun
    PHILIP_APOSTLE,             // Warm brown beard, olive-green cloak, processional cross staff & two barley loaves
    NATHANAEL_BARTHOLOMEW,      // Royal purple-trimmed tunic, fig branch & ancient scroll
    JUDE_AND_JAMES_JUST,        // White priestly linen & crimson mantle, tongue of Pentecost flame & medallion
    PAUL_OF_TARSUS,             // High forehead, long pointed dark beard, deep crimson cloak, Sword of the Spirit & epistle scroll
    MARY_MOTHER_OF_JESUS,       // Imperial Tyrian crimson-purple maphorion veil with 3 golden stars, sapphire inner robe, white lily
    JOHN_THE_BAPTIST,           // Long ascetic hair & beard, woven camel-hair tunic, reed cross staff & Jordan shell
    MARY_MAGDALENE,             // Flowing auburn hair & rose-crimson veil, alabaster jar of spikenard & resurrection sunrise
    LUKE_PHYSICIAN,             // Scholarly bearded portrait, lapis & emerald robes, physician's vessel & twin scrolls
    MARK_EVANGELIST,            // Alexandrian cloak with golden winged lion emblem & papyrus codex
    STEPHEN_PROTOMARTYR,        // Youthful deacon in gilded crimson dalmatic, palm of martyrdom & open heaven crown
    TIMOTHY_AND_BARNABAS,       // Pastoral olive cloak, glowing oil lamp of truth & shepherd's staff
    GABRIEL_ARCHANGEL,          // Radiant 3D golden-white feathered wings, celestial diadem, herald staff & lily
    JOSEPH_CARPENTER,           // Warm Judean cloak, flowering almond staff, carpenter's square & Bethlehem lantern
    WISE_MAGI_KING,             // Jeweled Persian royal crown, velvet purple-gold mantle & golden casket of frankincense
    NICODEMUS_TEACHER,          // White & lapis striped Tallit prayer shawl,Sanhedrin headband, night lantern & Torah scroll
    SAMARITAN_WOMAN_WELL,       // Turquoise & terracotta veil, sculpted clay water amphora & living water spring
    MARTHA_AND_MARY_BETHANY,    // Saffron & rose-violet robes, glowing hospitality oil lamp & anointing vessel
    LAZARUS_OF_BETHANY,         // White linen bands & emerald cloak of new life, olive branch & rolled tomb stone
    ZACCHAEUS_SEEKER,           // Rich emerald & gold tax-collector tunic, sycamore branch & pouch of restitution gold
    CORNELIUS_CENTURION,        // Gilded Roman galea helmet with crimson crest, bronze cuirass, red cloak & shield of faith
    LYDIA_OF_THYATIRA,          // Royal Tyrian purple silk veil & robe, scroll of Philippi & pearl clasp
    MOSES_AND_ELIJAH_PROPHETS,  // Flowing patriarch silver beard, sapphire tablets of the Law & mantle of fire
    MICHAEL_ARCHANGEL           // Blazing crimson-gold archangel wings, celestial armor, scales & starry sword
}

data class BiblicalCharacter(
    val id: String,
    val name: String,
    val title: String,
    val ancientName: String,
    val groupLabel: String, // "Christ the Lord", "The Twelve Disciples", "Apostles & Evangelists", "Holy Women & Prophets", "Gospel Witnesses & Angels"
    val visualStyle: CharacterVisualStyle,
    val primaryRobeColor: Color,
    val mantleColor: Color,
    val haloAccentColor: Color,
    val backgroundSkyTop: Color,
    val backgroundSkyBottom: Color,
    val sacredAttribute: String,
    val biography: String,
    val portraitSymbolism: String,
    val keyBookId: String,
    val keyChapterNumber: Int,
    val keyVerseQuote: String
)

object BiblicalCharactersCatalog {

    val allCharacters: List<BiblicalCharacter> = listOf(
        BiblicalCharacter(
            id = "JESUS",
            name = "Jesus Christ",
            title = "The Eternal Word, Good Shepherd & Risen Messiah",
            ancientName = "Yeshua HaMashiach • ΙΗΣΟΥΣ ΧΡΙΣΤΟΣ",
            groupLabel = "Christ the Lord",
            visualStyle = CharacterVisualStyle.JESUS_PANTOCRATOR,
            primaryRobeColor = Color(0xFF9E1F38),
            mantleColor = Color(0xFF1B4B8A),
            haloAccentColor = Color(0xFFFFD86B),
            backgroundSkyTop = Color(0xFF141B3D),
            backgroundSkyBottom = Color(0xFF6B3A5C),
            sacredAttribute = "Cruciform Halo, Blessing Right Hand & Jeweled Gospel Book",
            biography = "The central figure of the entire New Testament—born in Bethlehem, baptized in the Jordan, proclaiming the Kingdom of Heaven across Galilee and Judea, crucified at Calvary for the redemption of the world, and risen in glory on the third day.",
            portraitSymbolism = "Depicted in classic 3D Byzantine-Renaissance iconography: the crimson inner tunic (chiton) signifies His humanity and redemptive sacrifice, draped with the royal lapis-lazuli mantle (himation) signifying His eternal divinity.",
            keyBookId = "JHN",
            keyChapterNumber = 1,
            keyVerseQuote = "I am the light of the world. He who follows me will not walk in the darkness, but will have the light of life."
        ),
        BiblicalCharacter(
            id = "PETER",
            name = "Simon Peter",
            title = "Prince of the Apostles & Fisher of Men",
            ancientName = "Shimon Kepha • Πετρος",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.SIMON_PETER,
            primaryRobeColor = Color(0xFF1D5E6E),
            mantleColor = Color(0xFFD9932E),
            haloAccentColor = Color(0xFFFFD166),
            backgroundSkyTop = Color(0xFF0E2A3D),
            backgroundSkyBottom = Color(0xFF286078),
            sacredAttribute = "Gold & Silver Keys of the Kingdom & Galilean Fishing Net",
            biography = "A Galilean fisherman from Bethsaida called by Jesus with his brother Andrew. First to confess Jesus as the Christ at Caesarea Philippi, Peter was restored by the risen Lord by the Sea of Tiberias and preached with power at Pentecost.",
            portraitSymbolism = "Portrayed with a curly silver-white beard, deep sea-teal tunic recalling the Sea of Galilee, warm ochre-gold mantle, and holding the crossed Golden and Silver Keys of the Kingdom.",
            keyBookId = "MAT",
            keyChapterNumber = 16,
            keyVerseQuote = "You are the Christ, the Son of the living God."
        ),
        BiblicalCharacter(
            id = "JOHN",
            name = "John the Beloved",
            title = "Apostle of Love, Evangelist & Seer of Patmos",
            ancientName = "Yochanan • Ιωαννης",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.JOHN_BELOVED,
            primaryRobeColor = Color(0xFF1D6B52),
            mantleColor = Color(0xFFA82440),
            haloAccentColor = Color(0xFFFFDA70),
            backgroundSkyTop = Color(0xFF15193B),
            backgroundSkyBottom = Color(0xFF472759),
            sacredAttribute = "Golden Chalice, Eagle of Heaven & Seven-Sealed Scroll",
            biography = "Son of Zebedee and closest confidant of Jesus, who leaned upon the Lord's breast at the Last Supper, stood faithfully with Mary beneath the Cross, and authored the Fourth Gospel, three Epistles, and Revelation.",
            portraitSymbolism = "Depicted with a serene youthful face, wearing an emerald tunic of eternal life and a crimson cloak of sacrificial love while holding the Gospel scroll.",
            keyBookId = "JHN",
            keyChapterNumber = 13,
            keyVerseQuote = "God is love, and he who remains in love remains in God, and God remains in him."
        ),
        BiblicalCharacter(
            id = "JAMES_GREATER",
            name = "James (Son of Zebedee)",
            title = "Son of Thunder & Witness of the Transfiguration",
            ancientName = "Ya'akov • Ιακωβος",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.JAMES_GREATER,
            primaryRobeColor = Color(0xFF2B4C7E),
            mantleColor = Color(0xFF8C3B2B),
            haloAccentColor = Color(0xFFF5C65D),
            backgroundSkyTop = Color(0xFF1A223D),
            backgroundSkyBottom = Color(0xFF4F3859),
            sacredAttribute = "Apostolic Pilgrim Staff, Scroll & Scallop Shell",
            biography = "Elder brother of John and one of the inner circle of three disciples privileged to witness the raising of Jairus' daughter, the Transfiguration on Mount Tabor, and Jesus' prayer in Gethsemane.",
            portraitSymbolism = "Rendered with a dark, vigorous beard, warm russet traveler's cloak, and pilgrim staff symbolizing his fearless missionary zeal.",
            keyBookId = "MAT",
            keyChapterNumber = 17,
            keyVerseQuote = "Jesus took with him Peter, James, and John his brother, and brought them up into a high mountain by themselves."
        ),
        BiblicalCharacter(
            id = "ANDREW",
            name = "Andrew the First-Called",
            title = "Protokletos & Fisherman of Bethsaida",
            ancientName = "Andreas • Ανδρεας",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.ANDREW_APOSTLE,
            primaryRobeColor = Color(0xFF1B527A),
            mantleColor = Color(0xFF2E7D6B),
            haloAccentColor = Color(0xFFFFCF60),
            backgroundSkyTop = Color(0xFF0E243B),
            backgroundSkyBottom = Color(0xFF1E5266),
            sacredAttribute = "Diagonal Saltire Cross, Fishing Net & Two Fishes",
            biography = "Originally a disciple of John the Baptist who immediately brought his brother Simon Peter to Jesus with the words, 'We have found the Messiah!'",
            portraitSymbolism = "Shown with flowing silver hair and beard, dressed in deep Mediterranean azure and emerald green with the diagonal cross.",
            keyBookId = "JHN",
            keyChapterNumber = 1,
            keyVerseQuote = "He first found his own brother, Simon, and said to him, 'We have found the Messiah!'"
        ),
        BiblicalCharacter(
            id = "MATTHEW",
            name = "Matthew (Levi)",
            title = "Apostle & Author of the First Gospel",
            ancientName = "Mattityahu • Ματθαιος",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.MATTHEW_EVANGELIST,
            primaryRobeColor = Color(0xFF7A1F3D),
            mantleColor = Color(0xFFC98A2C),
            haloAccentColor = Color(0xFFFFD86B),
            backgroundSkyTop = Color(0xFF291538),
            backgroundSkyBottom = Color(0xFF5E2B4C),
            sacredAttribute = "Illuminated Gospel Codex, Quill & Winged Messianic Emblem",
            biography = "Called by Jesus from the tax booth in Capernaum, Matthew left all to follow Him, hosted a great banquet for sinners to meet the Savior, and recorded the royal Gospel of the Kingdom.",
            portraitSymbolism = "Clothed in regal burgundy and burnished gold leaf, holding an open illuminated codex and scribe's stylus.",
            keyBookId = "MAT",
            keyChapterNumber = 9,
            keyVerseQuote = "He said to him, 'Follow me.' He got up and followed him."
        ),
        BiblicalCharacter(
            id = "THOMAS",
            name = "Thomas (Didymus)",
            title = "Bold Confessor of the Risen Lord",
            ancientName = "Te'oma • Θωμας",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.THOMAS_APOSTLE,
            primaryRobeColor = Color(0xFF8C3B2B),
            mantleColor = Color(0xFF2B5B84),
            haloAccentColor = Color(0xFFF7C95C),
            backgroundSkyTop = Color(0xFF241A38),
            backgroundSkyBottom = Color(0xFF5E364B),
            sacredAttribute = "Architect's Rule, Lance of Faith & Radiant Wounds of Christ",
            biography = "Known for his loyal courage and his unforgettable proclamation upon beholding the risen Jesus in the Upper Room: 'My Lord and my God!'",
            portraitSymbolism = "Portrayed with an earnest gaze, warm terracotta-red cloak, and carpenter/builder's rule recalling his mission to the East.",
            keyBookId = "JHN",
            keyChapterNumber = 20,
            keyVerseQuote = "Thomas answered him, 'My Lord and my God!'"
        ),
        BiblicalCharacter(
            id = "PHILIP",
            name = "Philip of Bethsaida",
            title = "Herald of the Bread of Life & Guide to the Nations",
            ancientName = "Philippos • Φιλιππος",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.PHILIP_APOSTLE,
            primaryRobeColor = Color(0xFF2E6B47),
            mantleColor = Color(0xFFB87A28),
            haloAccentColor = Color(0xFFFFD269),
            backgroundSkyTop = Color(0xFF142B29),
            backgroundSkyBottom = Color(0xFF2E5C4A),
            sacredAttribute = "Cross Staff & Baskets of Loaves on the Galilean Hillside",
            biography = "Called directly by Jesus in Galilee, Philip invited Nathanael with the words 'Come and see,' and asked Jesus in the Upper Room, 'Lord, show us the Father.'",
            portraitSymbolism = "Dressed in Galilean olive-emerald and warm harvest amber, holding the tall cross-staff and barley loaves.",
            keyBookId = "JHN",
            keyChapterNumber = 14,
            keyVerseQuote = "Philip said to him, 'Lord, show us the Father, and that will be enough for us.'"
        ),
        BiblicalCharacter(
            id = "NATHANAEL",
            name = "Nathanael (Bartholomew)",
            title = "True Israelite in Whom Is No Deceit",
            ancientName = "Netan'el Bar-Talmai • Ναθαναηλ",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.NATHANAEL_BARTHOLOMEW,
            primaryRobeColor = Color(0xFF562B7A),
            mantleColor = Color(0xFFD9B88F),
            haloAccentColor = Color(0xFFFFD86B),
            backgroundSkyTop = Color(0xFF1F1638),
            backgroundSkyBottom = Color(0xFF4A2E66),
            sacredAttribute = "Fig Tree Leaf, Royal Scroll & Confession of the Son of God",
            biography = "Marveled when Jesus saw him praying beneath the fig tree before Philip called him, immediately acclaiming Jesus as 'the Son of God, the King of Israel!'",
            portraitSymbolism = "Wearing a white-parchment mantle over royal amethyst purple, symbolizing guileless purity and royal confession.",
            keyBookId = "JHN",
            keyChapterNumber = 1,
            keyVerseQuote = "Rabbi, you are the Son of God! You are King of Israel!"
        ),
        BiblicalCharacter(
            id = "JAMES_AND_JUDE",
            name = "James the Just & Jude",
            title = "Pillars of Jerusalem & Authors of the General Epistles",
            ancientName = "Ya'akov & Yehudah • Ιακωβος & Ιουδας",
            groupLabel = "The Twelve Disciples",
            visualStyle = CharacterVisualStyle.JUDE_AND_JAMES_JUST,
            primaryRobeColor = Color(0xFF8F2436),
            mantleColor = Color(0xFFEAE0CE),
            haloAccentColor = Color(0xFFFFD463),
            backgroundSkyTop = Color(0xFF261730),
            backgroundSkyBottom = Color(0xFF593048),
            sacredAttribute = "Linen Ephod, Flame of Pentecost & Scroll of Living Faith",
            biography = "Leaders of the Jerusalem church and authors of the Epistles of James and Jude, calling believers to active wisdom, mercy to the poor, and contending earnestly for the faith.",
            portraitSymbolism = "Shown in priestly white linen over deep crimson with the golden flame of the Holy Spirit resting above.",
            keyBookId = "JAS",
            keyChapterNumber = 1,
            keyVerseQuote = "Every good gift and every perfect gift is from above, coming down from the Father of lights."
        ),
        BiblicalCharacter(
            id = "PAUL",
            name = "Paul of Tarsus",
            title = "Apostle to the Gentiles & Author of Thirteen Epistles",
            ancientName = "Sha'ul • Παυλος",
            groupLabel = "Apostles & Evangelists",
            visualStyle = CharacterVisualStyle.PAUL_OF_TARSUS,
            primaryRobeColor = Color(0xFF234775),
            mantleColor = Color(0xFF962135),
            haloAccentColor = Color(0xFFFFD86B),
            backgroundSkyTop = Color(0xFF1A1538),
            backgroundSkyBottom = Color(0xFF592B4A),
            sacredAttribute = "Sword of the Spirit, Damascus Light & Thirteen Epistle Scrolls",
            biography = "Transformed by the blinding glory of the risen Christ on the road to Damascus, Paul carried the Gospel across Asia Minor, Greece, and Rome.",
            portraitSymbolism = "Depicted with a high scholarly brow, long dark beard, deep crimson mantle of martyrdom, and the Sword of the Spirit.",
            keyBookId = "ACT",
            keyChapterNumber = 9,
            keyVerseQuote = "For to me to live is Christ, and to die is gain."
        ),
        BiblicalCharacter(
            id = "MARY_MOTHER",
            name = "Mary (Mother of Jesus)",
            title = "Theotokos, Handmaid of the Lord & Mother of the Messiah",
            ancientName = "Miriam • Μαριαμ",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.MARY_MOTHER_OF_JESUS,
            primaryRobeColor = Color(0xFF1D4D8C),
            mantleColor = Color(0xFF8C1D3B),
            haloAccentColor = Color(0xFFFFDF73),
            backgroundSkyTop = Color(0xFF141E42),
            backgroundSkyBottom = Color(0xFF4B2E63),
            sacredAttribute = "Three-Starred Maphorion Veil, Lily of the Annunciation & Magnificat",
            biography = "The virgin of Nazareth who answered the Angel Gabriel with humble faith, sang the Magnificat, pondered all things in her heart at Bethlehem, stood by the Cross, and prayed in the Upper Room at Pentecost.",
            portraitSymbolism = "Clothed in a sapphire-blue inner tunic and Tyrian crimson-purple veil adorned with three golden stars.",
            keyBookId = "LUK",
            keyChapterNumber = 1,
            keyVerseQuote = "My soul magnifies the Lord. My spirit has rejoiced in God my Savior."
        ),
        BiblicalCharacter(
            id = "JOHN_BAPTIST",
            name = "John the Baptist",
            title = "Forerunner of the Lord & Voice in the Wilderness",
            ancientName = "Yochanan HaMatbil • Ιωαννης ο Βαπτιστης",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.JOHN_THE_BAPTIST,
            primaryRobeColor = Color(0xFF8C6239),
            mantleColor = Color(0xFF265C4B),
            haloAccentColor = Color(0xFFFFD15C),
            backgroundSkyTop = Color(0xFF0F2B38),
            backgroundSkyBottom = Color(0xFF28666E),
            sacredAttribute = "Camel-Hair Garment, Reed Cross Staff & Waters of the Jordan",
            biography = "Son of Zechariah and Elizabeth who prepared the way of the Lord in the Judean wilderness, baptized Jesus in the Jordan River, and testified: 'Behold, the Lamb of God!'",
            portraitSymbolism = "Rendered with long prophetic hair and beard, desert camel-hair tunic, emerald mantle, and tall reed cross.",
            keyBookId = "JHN",
            keyChapterNumber = 1,
            keyVerseQuote = "Behold, the Lamb of God, who takes away the sin of the world!"
        ),
        BiblicalCharacter(
            id = "MARY_MAGDALENE",
            name = "Mary Magdalene",
            title = "First Witness of the Resurrection & Apostle to the Apostles",
            ancientName = "Miriam of Magdala • Μαρια η Μαγδαληνη",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.MARY_MAGDALENE,
            primaryRobeColor = Color(0xFFA62644),
            mantleColor = Color(0xFF3B2866),
            haloAccentColor = Color(0xFFFFDA75),
            backgroundSkyTop = Color(0xFF24173B),
            backgroundSkyBottom = Color(0xFF733252),
            sacredAttribute = "Alabaster Jar of Myrrh & Golden Sunrise at the Empty Tomb",
            biography = "Healed by Jesus in Galilee, Mary Magdalene supported His ministry, stood faithfully at Calvary, and was the first person to behold the risen Christ on Easter morning.",
            portraitSymbolism = "Portrayed in rose-crimson and twilight violet, holding the white alabaster jar of fragrant ointment.",
            keyBookId = "JHN",
            keyChapterNumber = 20,
            keyVerseQuote = "Mary Magdalene came and told the disciples that she had seen the Lord."
        ),
        BiblicalCharacter(
            id = "LUKE",
            name = "Luke the Evangelist",
            title = "Beloved Physician, Historian of Acts & Gospel Author",
            ancientName = "Loukas • Λουκας",
            groupLabel = "Apostles & Evangelists",
            visualStyle = CharacterVisualStyle.LUKE_PHYSICIAN,
            primaryRobeColor = Color(0xFF1F5E5B),
            mantleColor = Color(0xFF7A2842),
            haloAccentColor = Color(0xFFFFD469),
            backgroundSkyTop = Color(0xFF12243B),
            backgroundSkyBottom = Color(0xFF2B4F66),
            sacredAttribute = "Twin Scrolls of Luke-Acts, Physician's Vessel & Winged Ox",
            biography = "A Greek physician and faithful traveling companion of Paul from Troas to Rome who investigated all things carefully from the beginning to write the Gospel of mercy and the Acts of the Apostles.",
            portraitSymbolism = "Depicted in healing emerald-teal and scholarly burgundy, holding the twin volumes of the Gospel and Acts.",
            keyBookId = "LUK",
            keyChapterNumber = 1,
            keyVerseQuote = "It seemed good to me also, having traced the course of all things accurately from the first, to write to you in order."
        ),
        BiblicalCharacter(
            id = "MARK",
            name = "John Mark",
            title = "Evangelist of the Living Lion & Interpreter of Peter",
            ancientName = "Yochanan Marcus • Μαρκος",
            groupLabel = "Apostles & Evangelists",
            visualStyle = CharacterVisualStyle.MARK_EVANGELIST,
            primaryRobeColor = Color(0xFF1F5473),
            mantleColor = Color(0xFFC96832),
            haloAccentColor = Color(0xFFF7C852),
            backgroundSkyTop = Color(0xFF122638),
            backgroundSkyBottom = Color(0xFF2E546B),
            sacredAttribute = "Winged Lion Medallion & Galilean Gospel Scroll",
            biography = "Son of Mary of Jerusalem whose house was a prayer gathering place for the early church, companion of Barnabas and Paul, and interpreter for Simon Peter in Rome.",
            portraitSymbolism = "Shown with an amber-terracotta mantle over deep sea blue, accompanied by the golden emblem of the Lion of Judah.",
            keyBookId = "MRK",
            keyChapterNumber = 1,
            keyVerseQuote = "The beginning of the Good News of Jesus Christ, the Son of God."
        ),
        BiblicalCharacter(
            id = "STEPHEN",
            name = "Stephen the Protomartyr",
            title = "Deacon Full of Grace, Power & Heavenly Vision",
            ancientName = "Stephanos • Στεφανος",
            groupLabel = "Apostles & Evangelists",
            visualStyle = CharacterVisualStyle.STEPHEN_PROTOMARTYR,
            primaryRobeColor = Color(0xFFB02842),
            mantleColor = Color(0xFFD9A33A),
            haloAccentColor = Color(0xFFFFDF73),
            backgroundSkyTop = Color(0xFF1D1438),
            backgroundSkyBottom = Color(0xFF5C284F),
            sacredAttribute = "Gilded Deacon Dalmatic, Crown of Glory & Open Heavens",
            biography = "Chosen among the Seven in Jerusalem to care for widows and perform great wonders, Stephen beheld the heavens opened and the Son of Man standing at the right hand of God.",
            portraitSymbolism = "Dressed in a crimson deacon's dalmatic embroidered with gold bands, looking upward toward a descending golden crown.",
            keyBookId = "ACT",
            keyChapterNumber = 7,
            keyVerseQuote = "Behold, I see the heavens opened, and the Son of Man standing at the right hand of God!"
        ),
        BiblicalCharacter(
            id = "TIMOTHY_BARNABAS",
            name = "Timothy & Barnabas",
            title = "Faithful Shepherd of Ephesus & Son of Encouragement",
            ancientName = "Timotheos & Bar-Nabba • Τιμοθεος",
            groupLabel = "Apostles & Evangelists",
            visualStyle = CharacterVisualStyle.TIMOTHY_AND_BARNABAS,
            primaryRobeColor = Color(0xFF2E6B59),
            mantleColor = Color(0xFF75468C),
            haloAccentColor = Color(0xFFF5CC66),
            backgroundSkyTop = Color(0xFF1B1D3B),
            backgroundSkyBottom = Color(0xFF3E3B6B),
            sacredAttribute = "Burning Pastoral Lamp, Olive Branch & Shepherd Staff",
            biography = "Barnabas welcomed Paul in Jerusalem and journeyed with him to Cyprus and Antioch, while young Timothy served as Paul's most trusted emissary and shepherd at Ephesus.",
            portraitSymbolism = "Portrayed with a warm pastoral lamp and olive branch symbolizing encouragement and unfeigned faith.",
            keyBookId = "2TI",
            keyChapterNumber = 1,
            keyVerseQuote = "For God didn't give us a spirit of fear, but of power, love, and self-control."
        ),
        // ---------------- 12 NEW DISTINCT BIBLE STORY CHARACTERS ----------------
        BiblicalCharacter(
            id = "GABRIEL_ARCHANGEL",
            name = "Archangel Gabriel",
            title = "Heavenly Herald of the Incarnation & Good News",
            ancientName = "Gavri'el • Γαβριηλ",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.GABRIEL_ARCHANGEL,
            primaryRobeColor = Color(0xFF28588E),
            mantleColor = Color(0xFFE8EEF7),
            haloAccentColor = Color(0xFFFFE57F),
            backgroundSkyTop = Color(0xFF12224A),
            backgroundSkyBottom = Color(0xFF385A94),
            sacredAttribute = "Radiant 3D Feathered Wings, Golden Herald Staff & White Lily",
            biography = "Standing in the presence of God, the Archangel Gabriel appeared to Zechariah in the Temple sanctuary and to the Virgin Mary in Nazareth to announce the birth of John the Baptist and Jesus the Son of the Most High.",
            portraitSymbolism = "Sculpted with luminous 3D white-and-gold feathered wings, sapphire celestial tunic, and a golden herald's sceptre.",
            keyBookId = "LUK",
            keyChapterNumber = 1,
            keyVerseQuote = "I am Gabriel, who stands in the presence of God; and I was sent to speak to you, and to bring you this good news."
        ),
        BiblicalCharacter(
            id = "JOSEPH_NAZARETH",
            name = "Joseph of Nazareth",
            title = "Righteous Son of David & Guardian of the Holy Family",
            ancientName = "Yosef Ben-David • Ιωσηφ",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.JOSEPH_CARPENTER,
            primaryRobeColor = Color(0xFF6E4B2E),
            mantleColor = Color(0xFF234D6E),
            haloAccentColor = Color(0xFFF5C963),
            backgroundSkyTop = Color(0xFF151E3B),
            backgroundSkyBottom = Color(0xFF3D3559),
            sacredAttribute = "Flowering Almond Staff, Carpenter's Square & Guiding Lantern",
            biography = "A righteous carpenter of the house of David who obeyed the angel of the Lord in dreams, sheltering Mary and the infant Jesus in Bethlehem, during the flight into Egypt, and in the workshop of Nazareth.",
            portraitSymbolism = "Portrayed in warm Judean cedar-brown and deep midnight indigo, holding the budding almond staff and glowing lantern.",
            keyBookId = "MAT",
            keyChapterNumber = 1,
            keyVerseQuote = "Joseph, son of David, don't be afraid to take to yourself Mary, your wife, for that which is conceived in her is of the Holy Spirit."
        ),
        BiblicalCharacter(
            id = "WISE_MAGI",
            name = "The Wise Magi of the East",
            title = "Royal Star-Seekers Bearing Gold, Frankincense & Myrrh",
            ancientName = "Magoi • Μαγοι απο ανατολων",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.WISE_MAGI_KING,
            primaryRobeColor = Color(0xFF5B247A),
            mantleColor = Color(0xFFD4972A),
            haloAccentColor = Color(0xFFFFDC73),
            backgroundSkyTop = Color(0xFF14143B),
            backgroundSkyBottom = Color(0xFF4A2566),
            sacredAttribute = "Jeweled Eastern Crown, Golden Casket & Star of Bethlehem",
            biography = "Noble astronomers and sages from the East who followed the Messianic star to Jerusalem and Bethlehem, falling down to worship the young Child and opening their treasures of gold, frankincense, and myrrh.",
            portraitSymbolism = "Crowned with a 3D jeweled Persian diadem, wearing royal amethyst and brocaded gold while presenting an ornate golden reliquary.",
            keyBookId = "MAT",
            keyChapterNumber = 2,
            keyVerseQuote = "When they saw the star, they rejoiced with exceedingly great joy."
        ),
        BiblicalCharacter(
            id = "NICODEMUS_PHARISEE",
            name = "Nicodemus",
            title = "Ruler of the Jews & Night Seeker of Rebirth in the Spirit",
            ancientName = "Nakdimon • Νικοδημος",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.NICODEMUS_TEACHER,
            primaryRobeColor = Color(0xFF1E3666),
            mantleColor = Color(0xFFEDE6D6),
            haloAccentColor = Color(0xFFF5C85B),
            backgroundSkyTop = Color(0xFF0E1836),
            backgroundSkyBottom = Color(0xFF283B66),
            sacredAttribute = "Striped Tallit Prayer Shawl, Night Lantern, Myrrh & Aloes",
            biography = "A respected Pharisee and member of the Sanhedrin who came to Jesus by night to hear of being born anew of water and the Spirit, defended Jesus before the council, and brought a hundred pounds of myrrh and aloes for His burial.",
            portraitSymbolism = "Draped in anivory-white and sapphire-striped tallit shawl over deep midnight blue, holding a glowing oil lamp in the night.",
            keyBookId = "JHN",
            keyChapterNumber = 3,
            keyVerseQuote = "Rabbi, we know that you are a teacher come from God, for no one can do these signs that you do, unless God is with him."
        ),
        BiblicalCharacter(
            id = "SAMARITAN_WOMAN",
            name = "Woman of Samaria",
            title = "Witness at Jacob's Well & Herald of Living Water",
            ancientName = "Photini of Sychar • Η Γυνη η Σαμαρειτις",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.SAMARITAN_WOMAN_WELL,
            primaryRobeColor = Color(0xFF1A6B75),
            mantleColor = Color(0xFFC9643B),
            haloAccentColor = Color(0xFFFFD86B),
            backgroundSkyTop = Color(0xFF103040),
            backgroundSkyBottom = Color(0xFF246673),
            sacredAttribute = "Terracotta Water Jar & Stone Wellspring of Jacob",
            biography = "Meeting Jesus at Jacob's well at noon in Sychar, she received the revelation of Living Water and worship in Spirit and truth, leaving her water pot behind to bring her entire city to believe in the Savior of the world.",
            portraitSymbolism = "Portrayed in vibrant turquoise-water and terracotta robes beside the stone well with a sculpted clay amphora.",
            keyBookId = "JHN",
            keyChapterNumber = 4,
            keyVerseQuote = "Sir, give me this water, so that I don't get thirsty, neither come all the way here to draw."
        ),
        BiblicalCharacter(
            id = "MARTHA_AND_MARY_BETHANY",
            name = "Mary & Martha of Bethany",
            title = "Devoted Sisters of Hospitality, Contemplation & Resurrection Faith",
            ancientName = "Marta & Miriam • Μαρθα & Μαρια",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.MARTHA_AND_MARY_BETHANY,
            primaryRobeColor = Color(0xFF8C2D52),
            mantleColor = Color(0xFFD99A38),
            haloAccentColor = Color(0xFFFFDA70),
            backgroundSkyTop = Color(0xFF241738),
            backgroundSkyBottom = Color(0xFF5E2E52),
            sacredAttribute = "Lamp of Hospitality, Spikenard Flask & Choosing the Good Part",
            biography = "Beloved friends of Jesus who welcomed Him into their home in Bethany; Mary sat listening at the Lord's feet and anointed Him with costly spikenard, while Martha confessed Him as the Resurrection and the Life.",
            portraitSymbolism = "Clothed in warm saffron-gold and rose-burgundy, holding the lamp of faithful service and the vessel of devotion.",
            keyBookId = "LUK",
            keyChapterNumber = 10,
            keyVerseQuote = "Yes, Lord. I have come to believe that you are the Christ, God's Son, he who comes into the world."
        ),
        BiblicalCharacter(
            id = "LAZARUS_BETHANY",
            name = "Lazarus of Bethany",
            title = "Friend of Christ Called Forth from the Tomb",
            ancientName = "El'azar • Λαζαρος",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.LAZARUS_OF_BETHANY,
            primaryRobeColor = Color(0xFF26664E),
            mantleColor = Color(0xFFF2ECE1),
            haloAccentColor = Color(0xFFFFDF75),
            backgroundSkyTop = Color(0xFF142926),
            backgroundSkyBottom = Color(0xFF2E5E4E),
            sacredAttribute = "Unbound White Linen Shroud, Olive Branch & Open Garden Cave",
            biography = "Brother of Mary and Martha whom Jesus loved deeply; raised back to life after four days in the tomb when Jesus cried with a loud voice, 'Lazarus, come forth!'—a sign that led many in Jerusalem to believe.",
            portraitSymbolism = "Shown emerging into golden daylight draped in resurrection-white linen and an emerald cloak of renewed life.",
            keyBookId = "JHN",
            keyChapterNumber = 11,
            keyVerseQuote = "He cried with a loud voice, 'Lazarus, come out!' He who was dead came out."
        ),
        BiblicalCharacter(
            id = "ZACCHAEUS_JERICHO",
            name = "Zacchaeus of Jericho",
            title = "Repentant Seeker in the Sycamore Tree & Son of Abraham",
            ancientName = "Zakkai • Ζακχαιος",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.ZACCHAEUS_SEEKER,
            primaryRobeColor = Color(0xFF286342),
            mantleColor = Color(0xFFC98A28),
            haloAccentColor = Color(0xFFF7CE63),
            backgroundSkyTop = Color(0xFF172B22),
            backgroundSkyBottom = Color(0xFF345E42),
            sacredAttribute = "Sycamore Fig Foliage & Pouch of Fourfold Restitution",
            biography = "Chief tax collector of Jericho who climbed a sycamore tree to see Jesus pass by; overjoyed when Jesus called him by name to stay at his house, he pledged half his goods to the poor and fourfold restitution.",
            portraitSymbolism = "Portrayed amidst emerald sycamore leaves in a richly embroidered tunic, offering a golden pouch of generosity.",
            keyBookId = "LUK",
            keyChapterNumber = 19,
            keyVerseQuote = "Today, salvation has come to this house, because he also is a son of Abraham."
        ),
        BiblicalCharacter(
            id = "CORNELIUS_CENTURION",
            name = "Cornelius the Centurion",
            title = "God-Fearer of Caesarea & Firstfruits of the Gentiles",
            ancientName = "Cornelius • Κορνηλιος",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.CORNELIUS_CENTURION,
            primaryRobeColor = Color(0xFF9E2432),
            mantleColor = Color(0xFFC99738),
            haloAccentColor = Color(0xFFFFD86B),
            backgroundSkyTop = Color(0xFF261829),
            backgroundSkyBottom = Color(0xFF5C2836),
            sacredAttribute = "Crested Roman Helmet, Bronze Cuirass & Shield of Faith",
            biography = "Centurion of the Italian Cohort in Caesarea whose prayers and alms ascended as a memorial before God; visited by an angel and welcomed into the church with his household when the Holy Spirit fell during Peter's preaching.",
            portraitSymbolism = "Sculpted with a gleaming 3D Roman bronze cuirass, crimson centurion cloak, and crested helmet beneath the Dove of the Spirit.",
            keyBookId = "ACT",
            keyChapterNumber = 10,
            keyVerseQuote = "Your prayers and your gifts to the needy have gone up for a memorial before God."
        ),
        BiblicalCharacter(
            id = "LYDIA_PRISCILLA",
            name = "Lydia & Priscilla",
            title = "Pioneers of the European Church & Teachers of the Way",
            ancientName = "Lydia & Prisca • Λυδια & Πρισκιλλα",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.LYDIA_OF_THYATIRA,
            primaryRobeColor = Color(0xFF6B1D7A),
            mantleColor = Color(0xFFD94F70),
            haloAccentColor = Color(0xFFFFD970),
            backgroundSkyTop = Color(0xFF241236),
            backgroundSkyBottom = Color(0xFF592466),
            sacredAttribute = "Royal Tyrian Purple Tapestry, Scroll of Doctrine & House Lamp",
            biography = "Lydia, whose heart the Lord opened by the river in Philippi to host the first European church, and Priscilla, fellow tentmaker with Paul who instructed Apollos more accurately in the way of God.",
            portraitSymbolism = "Draped in luminous Tyrian purple silk with golden clasps and an illuminated scroll of hospitality and wisdom.",
            keyBookId = "ACT",
            keyChapterNumber = 16,
            keyVerseQuote = "A certain woman named Lydia, a seller of purple, of the city of Thyatira, one who worshiped God, heard us; whose heart the Lord opened."
        ),
        BiblicalCharacter(
            id = "MOSES_AND_ELIJAH",
            name = "Moses & Elijah",
            title = "Witnesses of the Law and the Prophets in Transfigured Glory",
            ancientName = "Moshe & Eliyahu • Μωυσης & Ηλιας",
            groupLabel = "Holy Women & Prophets",
            visualStyle = CharacterVisualStyle.MOSES_AND_ELIJAH_PROPHETS,
            primaryRobeColor = Color(0xFF234B82),
            mantleColor = Color(0xFFB84A28),
            haloAccentColor = Color(0xFFFFE078),
            backgroundSkyTop = Color(0xFF142042),
            backgroundSkyBottom = Color(0xFF42366E),
            sacredAttribute = "Sapphire Tablets of the Covenant & Fiery Prophetic Mantle",
            biography = "Appearing in radiant glory on the Mount of Transfiguration, Moses (representing the Law) and Elijah (representing the Prophets) spoke with Jesus concerning His departure which He was about to accomplish at Jerusalem.",
            portraitSymbolism = "Rendered with venerable flowing silver beards, holding the sapphire-stone Tablets of the Covenant surrounded by transfiguration fire.",
            keyBookId = "LUK",
            keyChapterNumber = 9,
            keyVerseQuote = "Behold, two men were talking with him, who were Moses and Elijah, who appeared in glory."
        ),
        BiblicalCharacter(
            id = "MICHAEL_ARCHANGEL",
            name = "Archangel Michael",
            title = "Prince of the Heavenly Host & Champion over the Dragon",
            ancientName = "Mikha'el • Μιχαηλ",
            groupLabel = "Gospel Witnesses & Angels",
            visualStyle = CharacterVisualStyle.MICHAEL_ARCHANGEL,
            primaryRobeColor = Color(0xFFA81E38),
            mantleColor = Color(0xFF1F4D8C),
            haloAccentColor = Color(0xFFFFE073),
            backgroundSkyTop = Color(0xFF1C1238),
            backgroundSkyBottom = Color(0xFF632042),
            sacredAttribute = "Blazing 3D Crimson-Gold Wings, Celestial Armor & Starry Sword",
            biography = "Leader of the heavenly armies in Jude and Revelation 12 who leads the angels in triumphant battle against the dragon, proclaiming the victory of the blood of the Lamb and the word of their testimony.",
            portraitSymbolism = "Sculpted with majestic 3D gold-and-crimson wings, shining celestial breastplate, and a blazing sword of light.",
            keyBookId = "REV",
            keyChapterNumber = 12,
            keyVerseQuote = "There was war in heaven: Michael and his angels made war on the dragon."
        )
    )

    fun getCharacterById(id: String): BiblicalCharacter {
        return allCharacters.firstOrNull { it.id == id } ?: allCharacters.first()
    }

    /**
     * Returns a rich cast of at least 6-8 distinct biblical characters for any chapter,
     * so every chapter in the Reader and Gallery displays a diverse, authentic ensemble!
     */
    fun getCharactersForChapter(bookId: String, chapterNumber: Int): List<BiblicalCharacter> {
        val baseIds = when (bookId) {
            "MAT" -> when (chapterNumber) {
                1 -> listOf("JESUS", "MARY_MOTHER", "JOSEPH_NAZARETH", "GABRIEL_ARCHANGEL", "MATTHEW", "MOSES_AND_ELIJAH")
                2 -> listOf("JESUS", "WISE_MAGI", "MARY_MOTHER", "JOSEPH_NAZARETH", "GABRIEL_ARCHANGEL", "MATTHEW")
                3 -> listOf("JESUS", "JOHN_BAPTIST", "GABRIEL_ARCHANGEL", "PETER", "ANDREW", "MATTHEW")
                4 -> listOf("JESUS", "PETER", "ANDREW", "JAMES_GREATER", "JOHN", "MICHAEL_ARCHANGEL")
                5, 6, 7 -> listOf("JESUS", "MATTHEW", "PETER", "JOHN", "PHILIP", "ANDREW", "THOMAS", "NATHANAEL")
                8 -> listOf("JESUS", "CORNELIUS_CENTURION", "PETER", "ANDREW", "JAMES_GREATER", "MATTHEW")
                9 -> listOf("JESUS", "MATTHEW", "PETER", "JOHN", "JAMES_GREATER", "MARTHA_AND_MARY_BETHANY")
                10 -> listOf("JESUS", "PETER", "ANDREW", "JAMES_GREATER", "JOHN", "MATTHEW", "THOMAS", "PHILIP")
                14 -> listOf("JESUS", "PETER", "PHILIP", "ANDREW", "JOHN_BAPTIST", "JOHN")
                16 -> listOf("JESUS", "PETER", "JOHN", "JAMES_GREATER", "ANDREW", "MATTHEW")
                17 -> listOf("JESUS", "MOSES_AND_ELIJAH", "PETER", "JAMES_GREATER", "JOHN", "MATTHEW")
                26 -> listOf("JESUS", "PETER", "JOHN", "JAMES_GREATER", "MARY_MAGDALENE", "MARTHA_AND_MARY_BETHANY")
                27 -> listOf("JESUS", "MARY_MOTHER", "MARY_MAGDALENE", "JOHN", "CORNELIUS_CENTURION", "NICODEMUS_PHARISEE")
                28 -> listOf("JESUS", "MARY_MAGDALENE", "MARY_MOTHER", "GABRIEL_ARCHANGEL", "PETER", "JOHN")
                else -> listOf("JESUS", "MATTHEW", "PETER", "JOHN", "PHILIP", "THOMAS")
            }
            "MRK" -> when (chapterNumber) {
                1 -> listOf("JESUS", "JOHN_BAPTIST", "PETER", "ANDREW", "JAMES_GREATER", "MARK")
                2, 3, 4 -> listOf("JESUS", "PETER", "ANDREW", "JAMES_GREATER", "JOHN", "MATTHEW", "MARK")
                5 -> listOf("JESUS", "PETER", "JAMES_GREATER", "JOHN", "MARTHA_AND_MARY_BETHANY", "MARK")
                6 -> listOf("JESUS", "JOHN_BAPTIST", "PHILIP", "ANDREW", "PETER", "MARK")
                9 -> listOf("JESUS", "MOSES_AND_ELIJAH", "PETER", "JAMES_GREATER", "JOHN", "MARK")
                14, 15, 16 -> listOf("JESUS", "MARY_MAGDALENE", "PETER", "JOHN", "CORNELIUS_CENTURION", "MARK")
                else -> listOf("JESUS", "PETER", "MARK", "JOHN", "JAMES_GREATER", "ANDREW")
            }
            "LUK" -> when (chapterNumber) {
                1 -> listOf("GABRIEL_ARCHANGEL", "MARY_MOTHER", "JOHN_BAPTIST", "JOSEPH_NAZARETH", "JESUS", "LUKE")
                2 -> listOf("JESUS", "MARY_MOTHER", "JOSEPH_NAZARETH", "GABRIEL_ARCHANGEL", "LUKE", "MOSES_AND_ELIJAH")
                3 -> listOf("JOHN_BAPTIST", "JESUS", "LUKE", "PETER", "ANDREW", "JOSEPH_NAZARETH")
                4, 5 -> listOf("JESUS", "PETER", "JAMES_GREATER", "JOHN", "ANDREW", "LUKE")
                7 -> listOf("JESUS", "CORNELIUS_CENTURION", "MARY_MAGDALENE", "JOHN_BAPTIST", "PETER", "LUKE")
                9 -> listOf("JESUS", "MOSES_AND_ELIJAH", "PETER", "JOHN", "JAMES_GREATER", "PHILIP")
                10 -> listOf("JESUS", "MARTHA_AND_MARY_BETHANY", "LUKE", "PETER", "JOHN", "PHILIP")
                19 -> listOf("JESUS", "ZACCHAEUS_JERICHO", "PETER", "JOHN", "PHILIP", "LUKE")
                22, 23, 24 -> listOf("JESUS", "PETER", "JOHN", "MARY_MAGDALENE", "GABRIEL_ARCHANGEL", "LUKE")
                else -> listOf("JESUS", "LUKE", "PETER", "JOHN", "MARTHA_AND_MARY_BETHANY", "ZACCHAEUS_JERICHO")
            }
            "JHN" -> when (chapterNumber) {
                1 -> listOf("JESUS", "JOHN_BAPTIST", "ANDREW", "PETER", "PHILIP", "NATHANAEL", "JOHN")
                2 -> listOf("JESUS", "MARY_MOTHER", "JOHN", "NATHANAEL", "PETER", "PHILIP")
                3 -> listOf("JESUS", "NICODEMUS_PHARISEE", "JOHN_BAPTIST", "JOHN", "PETER", "ANDREW")
                4 -> listOf("JESUS", "SAMARITAN_WOMAN", "JOHN", "PETER", "PHILIP", "ANDREW")
                6 -> listOf("JESUS", "PHILIP", "ANDREW", "PETER", "JOHN", "MOSES_AND_ELIJAH")
                11 -> listOf("JESUS", "LAZARUS_BETHANY", "MARTHA_AND_MARY_BETHANY", "THOMAS", "JOHN", "PETER")
                12 -> listOf("JESUS", "MARTHA_AND_MARY_BETHANY", "LAZARUS_BETHANY", "PHILIP", "ANDREW", "JOHN")
                13, 14, 15, 16, 17 -> listOf("JESUS", "JOHN", "PETER", "THOMAS", "PHILIP", "JUDE_AND_JAMES")
                18, 19 -> listOf("JESUS", "JOHN", "MARY_MOTHER", "MARY_MAGDALENE", "NICODEMUS_PHARISEE", "PETER")
                20 -> listOf("JESUS", "MARY_MAGDALENE", "THOMAS", "PETER", "JOHN", "GABRIEL_ARCHANGEL")
                21 -> listOf("JESUS", "PETER", "JOHN", "THOMAS", "NATHANAEL", "JAMES_GREATER")
                else -> listOf("JESUS", "JOHN", "PETER", "PHILIP", "THOMAS", "NICODEMUS_PHARISEE")
            }
            "ACT" -> when (chapterNumber) {
                1, 2 -> listOf("JESUS", "PETER", "JOHN", "MARY_MOTHER", "JAMES_GREATER", "ANDREW", "LUKE")
                3, 4, 5 -> listOf("PETER", "JOHN", "TIMOTHY_BARNABAS", "STEPHEN", "MARK", "LUKE")
                6, 7 -> listOf("STEPHEN", "PHILIP", "JESUS", "MOSES_AND_ELIJAH", "PAUL", "PETER")
                8 -> listOf("PHILIP", "PETER", "JOHN", "STEPHEN", "PAUL", "LUKE")
                9 -> listOf("JESUS", "PAUL", "PETER", "TIMOTHY_BARNABAS", "LUKE", "MARK")
                10, 11 -> listOf("CORNELIUS_CENTURION", "PETER", "GABRIEL_ARCHANGEL", "TIMOTHY_BARNABAS", "PAUL", "LUKE")
                12 -> listOf("PETER", "GABRIEL_ARCHANGEL", "JAMES_GREATER", "MARK", "MARY_MOTHER", "LUKE")
                13, 14, 15 -> listOf("PAUL", "TIMOTHY_BARNABAS", "PETER", "JAMES_AND_JUDE", "MARK", "LUKE")
                16, 17, 18 -> listOf("PAUL", "LYDIA_PRISCILLA", "TIMOTHY_BARNABAS", "LUKE", "SILAS", "APOLLOS")
                else -> listOf("PAUL", "LUKE", "TIMOTHY_BARNABAS", "CORNELIUS_CENTURION", "LYDIA_PRISCILLA", "PETER")
            }
            "ROM", "1CO", "2CO", "GAL", "EPH", "PHP", "COL", "1TH", "2TH", "TIT", "PHM" -> {
                listOf("PAUL", "JESUS", "TIMOTHY_BARNABAS", "LYDIA_PRISCILLA", "LUKE", "CORNELIUS_CENTURION")
            }
            "1TI", "2TI" -> {
                listOf("PAUL", "TIMOTHY_BARNABAS", "LUKE", "MARK", "LYDIA_PRISCILLA", "JESUS")
            }
            "HEB" -> listOf("JESUS", "MOSES_AND_ELIJAH", "PAUL", "TIMOTHY_BARNABAS", "GABRIEL_ARCHANGEL", "PETER")
            "JAS" -> listOf("JAMES_AND_JUDE", "JESUS", "MOSES_AND_ELIJAH", "PETER", "PAUL", "JOHN")
            "1PE", "2PE" -> listOf("PETER", "JESUS", "MARK", "MOSES_AND_ELIJAH", "PAUL", "JOHN")
            "1JN", "2JN", "3JN" -> listOf("JOHN", "JESUS", "MARY_MOTHER", "PETER", "ANDREW", "PHILIP")
            "JUD" -> listOf("JAMES_AND_JUDE", "MICHAEL_ARCHANGEL", "JESUS", "MOSES_AND_ELIJAH", "PETER", "ENOCH")
            "REV" -> listOf("JESUS", "JOHN", "MICHAEL_ARCHANGEL", "GABRIEL_ARCHANGEL", "MOSES_AND_ELIJAH", "STEPHEN")
            else -> listOf("JESUS", "PETER", "JOHN", "PAUL", "MARY_MOTHER", "GABRIEL_ARCHANGEL")
        }
        return baseIds.map { getCharacterById(it) }.distinctBy { it.id }
    }
}

package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.model.ArtPalette
import com.example.model.BookInfo
import com.example.model.SceneArchetype
import com.example.model.TestamentCategory

object NewTestamentBooks {

    val allBooks: List<BookInfo> = listOf(
        // ==================== THE FOUR GOSPELS ====================
        BookInfo(
            id = "MAT",
            apiName = "Matthew",
            name = "Matthew",
            shortName = "Matt",
            greekTitle = "Kata Matthaion",
            author = "Matthew (Levi) the Apostle",
            dateWritten = "c. AD 55–65",
            chapterCount = 28,
            category = TestamentCategory.GOSPELS,
            summary = "Presents Jesus as the promised Messianic King of Israel, fulfilling Old Testament prophecy through the Sermon on the Mount, parables of the Kingdom, and the Great Commission.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF132247),
                skyMiddle = Color(0xFF28467A),
                skyHorizon = Color(0xFFEBA94B),
                primaryJewel = Color(0xFFD13B52),
                secondaryJewel = Color(0xFF2B7AC9),
                goldLeaf = Color(0xFFFFD166),
                foregroundSilhouette = Color(0xFF191528),
                waterOrGround = Color(0xFF1F4E5B)
            ),
            defaultArchetype = SceneArchetype.SERMON_MOUNT
        ),
        BookInfo(
            id = "MRK",
            apiName = "Mark",
            name = "Mark",
            shortName = "Mark",
            greekTitle = "Kata Markon",
            author = "John Mark",
            dateWritten = "c. AD 50–60",
            chapterCount = 16,
            category = TestamentCategory.GOSPELS,
            summary = "A vivid, fast-paced narrative of Jesus as the suffering Servant of God, emphasizing His authoritative miracles over nature, healing compassion, and sacrificial cross.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF0D2838),
                skyMiddle = Color(0xFF1E5B70),
                skyHorizon = Color(0xFFE07A4B),
                primaryJewel = Color(0xFF1EA896),
                secondaryJewel = Color(0xFFD9534F),
                goldLeaf = Color(0xFFF7C852),
                foregroundSilhouette = Color(0xFF111C24),
                waterOrGround = Color(0xFF123B4A)
            ),
            defaultArchetype = SceneArchetype.SEA_OF_GALILEE_BOAT
        ),
        BookInfo(
            id = "LUK",
            apiName = "Luke",
            name = "Luke",
            shortName = "Luke",
            greekTitle = "Kata Loukan",
            author = "Luke the Beloved Physician",
            dateWritten = "c. AD 59–63",
            chapterCount = 24,
            category = TestamentCategory.GOSPELS,
            summary = "A carefully researched historical account highlighting Jesus as the compassionate Savior of all humanity, rich with songs of praise, parables of grace, and prayer.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1A2A3A),
                skyMiddle = Color(0xFF345E6E),
                skyHorizon = Color(0xFFF2BC66),
                primaryJewel = Color(0xFF2FA37C),
                secondaryJewel = Color(0xFFC94A6A),
                goldLeaf = Color(0xFFFFD875),
                foregroundSilhouette = Color(0xFF18221F),
                waterOrGround = Color(0xFF24493E)
            ),
            defaultArchetype = SceneArchetype.GOOD_SHEPHERD
        ),
        BookInfo(
            id = "JHN",
            apiName = "John",
            name = "John",
            shortName = "John",
            greekTitle = "Kata Ioannen",
            author = "John the Beloved Apostle",
            dateWritten = "c. AD 85–90",
            chapterCount = 21,
            category = TestamentCategory.GOSPELS,
            summary = "The spiritual Gospel of the Eternal Word made flesh, structured around seven miraculous signs and seven 'I AM' declarations revealing eternal life in Christ.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF12183B),
                skyMiddle = Color(0xFF2D3270),
                skyHorizon = Color(0xFFF5C156),
                primaryJewel = Color(0xFF3E78D6),
                secondaryJewel = Color(0xFFB83B5E),
                goldLeaf = Color(0xFFFFDC73),
                foregroundSilhouette = Color(0xFF141326),
                waterOrGround = Color(0xFF1D2C5E)
            ),
            defaultArchetype = SceneArchetype.LAST_SUPPER_CHALICE
        ),

        // ==================== ACTS OF THE APOSTLES ====================
        BookInfo(
            id = "ACT",
            apiName = "Acts",
            name = "Acts",
            shortName = "Acts",
            greekTitle = "Praxeis Apostolon",
            author = "Luke the Physician",
            dateWritten = "c. AD 62–64",
            chapterCount = 28,
            category = TestamentCategory.ACTS,
            summary = "Chronicles the outpouring of the Holy Spirit at Pentecost and the unstoppable spread of the Gospel from Jerusalem to Judea, Samaria, and the heart of Rome.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF29132E),
                skyMiddle = Color(0xFF6B2446),
                skyHorizon = Color(0xFFF59B42),
                primaryJewel = Color(0xFFE0485A),
                secondaryJewel = Color(0xFFF2A93B),
                goldLeaf = Color(0xFFFFD56B),
                foregroundSilhouette = Color(0xFF21121F),
                waterOrGround = Color(0xFF2B3B5C)
            ),
            defaultArchetype = SceneArchetype.PENTECOST_FLAMES
        ),

        // ==================== PAULINE EPISTLES ====================
        BookInfo(
            id = "ROM",
            apiName = "Romans",
            name = "Romans",
            shortName = "Rom",
            greekTitle = "Pros Romaious",
            author = "Paul the Apostle",
            dateWritten = "c. AD 57",
            chapterCount = 16,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Paul's theological masterpiece expounding the righteousness of God, justification by faith alone, life in the Spirit, and the unbreakable love of Christ.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1A1838),
                skyMiddle = Color(0xFF3D346E),
                skyHorizon = Color(0xFFE69F45),
                primaryJewel = Color(0xFF9C3D54),
                secondaryJewel = Color(0xFF427AA1),
                goldLeaf = Color(0xFFF4C552),
                foregroundSilhouette = Color(0xFF1A162B),
                waterOrGround = Color(0xFF282442)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),
        BookInfo(
            id = "1CO",
            apiName = "1 Corinthians",
            name = "1 Corinthians",
            shortName = "1 Cor",
            greekTitle = "Pros Korinthious A",
            author = "Paul the Apostle",
            dateWritten = "c. AD 55",
            chapterCount = 16,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Addresses Christian unity, spiritual gifts, the Lord's Supper, the supremacy of divine love (Chapter 13), and the glorious bodily resurrection.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF14243E),
                skyMiddle = Color(0xFF2A4D78),
                skyHorizon = Color(0xFFEBAA52),
                primaryJewel = Color(0xFFC94A64),
                secondaryJewel = Color(0xFF3891A6),
                goldLeaf = Color(0xFFF7CE68),
                foregroundSilhouette = Color(0xFF16202E),
                waterOrGround = Color(0xFF1D3B58)
            ),
            defaultArchetype = SceneArchetype.TEMPLE_SOLOMON_PORCH
        ),
        BookInfo(
            id = "2CO",
            apiName = "2 Corinthians",
            name = "2 Corinthians",
            shortName = "2 Cor",
            greekTitle = "Pros Korinthious B",
            author = "Paul the Apostle",
            dateWritten = "c. AD 55–56",
            chapterCount = 13,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "A deeply personal letter revealing how God's transcendent power shines through human weakness, jars of clay, and the ministry of reconciliation.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1E1D3B),
                skyMiddle = Color(0xFF433E75),
                skyHorizon = Color(0xFFE6A15C),
                primaryJewel = Color(0xFF6B52B8),
                secondaryJewel = Color(0xFFD46A6A),
                goldLeaf = Color(0xFFF2C96B),
                foregroundSilhouette = Color(0xFF1B192E),
                waterOrGround = Color(0xFF2B284D)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),
        BookInfo(
            id = "GAL",
            apiName = "Galatians",
            name = "Galatians",
            shortName = "Gal",
            greekTitle = "Pros Galatas",
            author = "Paul the Apostle",
            dateWritten = "c. AD 48–49",
            chapterCount = 6,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "The Magna Carta of Christian liberty, proclaiming freedom from legalism through faith in Christ and the ninefold Fruit of the Holy Spirit.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1B2E2B),
                skyMiddle = Color(0xFF2D5950),
                skyHorizon = Color(0xFFF0B456),
                primaryJewel = Color(0xFF2E9E75),
                secondaryJewel = Color(0xFFD97236),
                goldLeaf = Color(0xFFF7D06A),
                foregroundSilhouette = Color(0xFF152421),
                waterOrGround = Color(0xFF22423A)
            ),
            defaultArchetype = SceneArchetype.SERMON_MOUNT
        ),
        BookInfo(
            id = "EPH",
            apiName = "Ephesians",
            name = "Ephesians",
            shortName = "Eph",
            greekTitle = "Pros Ephesious",
            author = "Paul the Apostle",
            dateWritten = "c. AD 60–62",
            chapterCount = 6,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Unfolds the cosmic mystery of the Church as the Body and Temple of Christ, saved by grace and equipped with the whole Armor of God.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF131D3B),
                skyMiddle = Color(0xFF26407A),
                skyHorizon = Color(0xFFF2B84B),
                primaryJewel = Color(0xFF3B72C9),
                secondaryJewel = Color(0xFFD49B35),
                goldLeaf = Color(0xFFFFD86B),
                foregroundSilhouette = Color(0xFF141B2E),
                waterOrGround = Color(0xFF1D2F59)
            ),
            defaultArchetype = SceneArchetype.ARMOR_OF_FAITH
        ),
        BookInfo(
            id = "PHP",
            apiName = "Philippians",
            name = "Philippians",
            shortName = "Phil",
            greekTitle = "Pros Philippesious",
            author = "Paul the Apostle",
            dateWritten = "c. AD 61",
            chapterCount = 4,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "The Epistle of Joy written from prison, celebrating the humble incarnation and exaltation of Christ and the peace of God that surpasses understanding.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF24183B),
                skyMiddle = Color(0xFF52347A),
                skyHorizon = Color(0xFFF7BE57),
                primaryJewel = Color(0xFF8C46B8),
                secondaryJewel = Color(0xFFE69138),
                goldLeaf = Color(0xFFFFDB70),
                foregroundSilhouette = Color(0xFF1E142E),
                waterOrGround = Color(0xFF33224F)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),
        BookInfo(
            id = "COL",
            apiName = "Colossians",
            name = "Colossians",
            shortName = "Col",
            greekTitle = "Pros Kolossaeis",
            author = "Paul the Apostle",
            dateWritten = "c. AD 60–62",
            chapterCount = 4,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Exalts the absolute supremacy and sufficiency of Christ—the visible image of the invisible God in whom all things hold together.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF10203B),
                skyMiddle = Color(0xFF214A7A),
                skyHorizon = Color(0xFFF2B652),
                primaryJewel = Color(0xFF2E8BC0),
                secondaryJewel = Color(0xFFC44D68),
                goldLeaf = Color(0xFFFFD666),
                foregroundSilhouette = Color(0xFF111C30),
                waterOrGround = Color(0xFF183659)
            ),
            defaultArchetype = SceneArchetype.THRONE_AND_RAINBOW
        ),
        BookInfo(
            id = "1TH",
            apiName = "1 Thessalonians",
            name = "1 Thessalonians",
            shortName = "1 Thess",
            greekTitle = "Pros Thessalonikeis A",
            author = "Paul the Apostle",
            dateWritten = "c. AD 50–51",
            chapterCount = 5,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Encourages young believers to stand firm in holiness, brotherly love, and joyful expectation of the Lord's glorious return.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF172642),
                skyMiddle = Color(0xFF2E5282),
                skyHorizon = Color(0xFFF5B759),
                primaryJewel = Color(0xFF4A8FE7),
                secondaryJewel = Color(0xFFE07A5F),
                goldLeaf = Color(0xFFFFD875),
                foregroundSilhouette = Color(0xFF162033),
                waterOrGround = Color(0xFF223C61)
            ),
            defaultArchetype = SceneArchetype.ASCENSION_CLOUDS
        ),
        BookInfo(
            id = "2TH",
            apiName = "2 Thessalonians",
            name = "2 Thessalonians",
            shortName = "2 Thess",
            greekTitle = "Pros Thessalonikeis B",
            author = "Paul the Apostle",
            dateWritten = "c. AD 51–52",
            chapterCount = 3,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Steadies the church regarding the Day of the Lord, calling believers to steadfast endurance and faithful diligence.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1E1A3D),
                skyMiddle = Color(0xFF3E3573),
                skyHorizon = Color(0xFFEB9C4D),
                primaryJewel = Color(0xFF7552CC),
                secondaryJewel = Color(0xFFD9534F),
                goldLeaf = Color(0xFFF5C963),
                foregroundSilhouette = Color(0xFF19152E),
                waterOrGround = Color(0xFF29244D)
            ),
            defaultArchetype = SceneArchetype.ANCHOR_OF_HOPE
        ),
        BookInfo(
            id = "1TI",
            apiName = "1 Timothy",
            name = "1 Timothy",
            shortName = "1 Tim",
            greekTitle = "Pros Timotheon A",
            author = "Paul the Apostle",
            dateWritten = "c. AD 62–64",
            chapterCount = 6,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Pastoral counsel to young Timothy on sound doctrine, shepherd leadership, godliness with contentment, and fighting the good fight of faith.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF261C18),
                skyMiddle = Color(0xFF593E31),
                skyHorizon = Color(0xFFEBAA4B),
                primaryJewel = Color(0xFFC87D32),
                secondaryJewel = Color(0xFF3B7A8C),
                goldLeaf = Color(0xFFF7CE68),
                foregroundSilhouette = Color(0xFF211814),
                waterOrGround = Color(0xFF3B2A22)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),
        BookInfo(
            id = "2TI",
            apiName = "2 Timothy",
            name = "2 Timothy",
            shortName = "2 Tim",
            greekTitle = "Pros Timotheon B",
            author = "Paul the Apostle",
            dateWritten = "c. AD 66–67",
            chapterCount = 4,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Paul's final testament from a Roman dungeon, charging Timothy to guard the sacred deposit, preach the Word, and look toward the crown of righteousness.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF241426),
                skyMiddle = Color(0xFF54284A),
                skyHorizon = Color(0xFFEBA045),
                primaryJewel = Color(0xFFB83B5E),
                secondaryJewel = Color(0xFFD49A35),
                goldLeaf = Color(0xFFFFD466),
                foregroundSilhouette = Color(0xFF1F1121),
                waterOrGround = Color(0xFF381C33)
            ),
            defaultArchetype = SceneArchetype.ARMOR_OF_FAITH
        ),
        BookInfo(
            id = "TIT",
            apiName = "Titus",
            name = "Titus",
            shortName = "Titus",
            greekTitle = "Pros Titon",
            author = "Paul the Apostle",
            dateWritten = "c. AD 63–65",
            chapterCount = 3,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "Guides Titus in organizing the churches across the island of Crete, demonstrating how saving grace trains believers for good works.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF10283B),
                skyMiddle = Color(0xFF22577A),
                skyHorizon = Color(0xFFF2B652),
                primaryJewel = Color(0xFF28968C),
                secondaryJewel = Color(0xFFD97736),
                goldLeaf = Color(0xFFFFD66B),
                foregroundSilhouette = Color(0xFF11212E),
                waterOrGround = Color(0xFF173F59)
            ),
            defaultArchetype = SceneArchetype.MEDITERRANEAN_VOYAGE
        ),
        BookInfo(
            id = "PHM",
            apiName = "Philemon",
            name = "Philemon",
            shortName = "Phlm",
            greekTitle = "Pros Philemona",
            author = "Paul the Apostle",
            dateWritten = "c. AD 60–62",
            chapterCount = 1,
            category = TestamentCategory.PAULINE_EPISTLES,
            summary = "A tender masterpiece of Christian forgiveness and brotherhood, appealing to Philemon to welcome back Onesimus no longer as a bondservant but as a beloved brother.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF291C2E),
                skyMiddle = Color(0xFF573B61),
                skyHorizon = Color(0xFFF0AD52),
                primaryJewel = Color(0xFFC45A75),
                secondaryJewel = Color(0xFF488B8F),
                goldLeaf = Color(0xFFF7CF68),
                foregroundSilhouette = Color(0xFF211624),
                waterOrGround = Color(0xFF3B2842)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),

        // ==================== GENERAL EPISTLES ====================
        BookInfo(
            id = "HEB",
            apiName = "Hebrews",
            name = "Hebrews",
            shortName = "Heb",
            greekTitle = "Pros Hebraious",
            author = "Apostolic Circle",
            dateWritten = "c. AD 64–68",
            chapterCount = 13,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Reveals Christ as our Great High Priest and Mediator of a better covenant, crowned with the Hall of Faith in Chapter 11.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1E173B),
                skyMiddle = Color(0xFF42317A),
                skyHorizon = Color(0xFFF2B249),
                primaryJewel = Color(0xFF8A4FFF),
                secondaryJewel = Color(0xFFD98A2B),
                goldLeaf = Color(0xFFFFDA6B),
                foregroundSilhouette = Color(0xFF19132E),
                waterOrGround = Color(0xFF2D2254)
            ),
            defaultArchetype = SceneArchetype.ANCHOR_OF_HOPE
        ),
        BookInfo(
            id = "JAS",
            apiName = "James",
            name = "James",
            shortName = "Jas",
            greekTitle = "Iakobou Epistole",
            author = "James, Brother of the Lord",
            dateWritten = "c. AD 45–49",
            chapterCount = 5,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "The Proverbs of the New Testament: a call to authentic, living faith expressed through wisdom, bridled speech, mercy to the poor, and fervent prayer.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF162B24),
                skyMiddle = Color(0xFF2B5949),
                skyHorizon = Color(0xFFEBAC4B),
                primaryJewel = Color(0xFF28996E),
                secondaryJewel = Color(0xFFD47A35),
                goldLeaf = Color(0xFFF7CF68),
                foregroundSilhouette = Color(0xFF12211C),
                waterOrGround = Color(0xFF1F4035)
            ),
            defaultArchetype = SceneArchetype.SERMON_MOUNT
        ),
        BookInfo(
            id = "1PE",
            apiName = "1 Peter",
            name = "1 Peter",
            shortName = "1 Pet",
            greekTitle = "Petrou A",
            author = "Peter the Apostle",
            dateWritten = "c. AD 62–64",
            chapterCount = 5,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Written to strengthen suffering believers with a living hope refined like gold through fire, built as living stones upon Christ the Cornerstone.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF14243B),
                skyMiddle = Color(0xFF284B78),
                skyHorizon = Color(0xFFF0AD4E),
                primaryJewel = Color(0xFF3B82C4),
                secondaryJewel = Color(0xFFD9534F),
                goldLeaf = Color(0xFFFFD466),
                foregroundSilhouette = Color(0xFF121C2E),
                waterOrGround = Color(0xFF1C3657)
            ),
            defaultArchetype = SceneArchetype.ANCHOR_OF_HOPE
        ),
        BookInfo(
            id = "2PE",
            apiName = "2 Peter",
            name = "2 Peter",
            shortName = "2 Pet",
            greekTitle = "Petrou B",
            author = "Peter the Apostle",
            dateWritten = "c. AD 65–67",
            chapterCount = 3,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Calls believers to grow in divine grace and knowledge, recalling the majesty of the Transfiguration and awaiting a new heavens and new earth.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF171E3D),
                skyMiddle = Color(0xFF30407D),
                skyHorizon = Color(0xFFF5B851),
                primaryJewel = Color(0xFF4E6CE0),
                secondaryJewel = Color(0xFFE08245),
                goldLeaf = Color(0xFFFFD970),
                foregroundSilhouette = Color(0xFF141930),
                waterOrGround = Color(0xFF232F5C)
            ),
            defaultArchetype = SceneArchetype.TRANSFIGURATION
        ),
        BookInfo(
            id = "1JN",
            apiName = "1 John",
            name = "1 John",
            shortName = "1 John",
            greekTitle = "Ioannou A",
            author = "John the Apostle",
            dateWritten = "c. AD 90–95",
            chapterCount = 5,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Proclaims that 'God is Light' and 'God is Love,' assuring believers of fellowship with the Father and eternal life through the Son.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF2B1329),
                skyMiddle = Color(0xFF612859),
                skyHorizon = Color(0xFFF7B952),
                primaryJewel = Color(0xFFD43F6A),
                secondaryJewel = Color(0xFF4A7FE0),
                goldLeaf = Color(0xFFFFDC73),
                foregroundSilhouette = Color(0xFF211020),
                waterOrGround = Color(0xFF421D3D)
            ),
            defaultArchetype = SceneArchetype.LAST_SUPPER_CHALICE
        ),
        BookInfo(
            id = "2JN",
            apiName = "2 John",
            name = "2 John",
            shortName = "2 John",
            greekTitle = "Ioannou B",
            author = "John the Elder",
            dateWritten = "c. AD 90–95",
            chapterCount = 1,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Exhorts the church to walk in truth and love while remaining vigilant in the doctrine of Christ's incarnation.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF261730),
                skyMiddle = Color(0xFF523066),
                skyHorizon = Color(0xFFF2B252),
                primaryJewel = Color(0xFFC24678),
                secondaryJewel = Color(0xFF3E8C84),
                goldLeaf = Color(0xFFF7CF68),
                foregroundSilhouette = Color(0xFF1E1226),
                waterOrGround = Color(0xFF382147)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),
        BookInfo(
            id = "3JN",
            apiName = "3 John",
            name = "3 John",
            shortName = "3 John",
            greekTitle = "Ioannou G",
            author = "John the Elder",
            dateWritten = "c. AD 90–95",
            chapterCount = 1,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Commends beloved Gaius for walking in the truth and showing generous hospitality to traveling servants of the Gospel.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF1A2836),
                skyMiddle = Color(0xFF32526E),
                skyHorizon = Color(0xFFF0B456),
                primaryJewel = Color(0xFF349688),
                secondaryJewel = Color(0xFFC95B5B),
                goldLeaf = Color(0xFFF7D16B),
                foregroundSilhouette = Color(0xFF15202B),
                waterOrGround = Color(0xFF243B4F)
            ),
            defaultArchetype = SceneArchetype.SCROLL_AND_LAMP
        ),
        BookInfo(
            id = "JUD",
            apiName = "Jude",
            name = "Jude",
            shortName = "Jude",
            greekTitle = "Iouda Epistole",
            author = "Jude, Brother of James",
            dateWritten = "c. AD 65–80",
            chapterCount = 1,
            category = TestamentCategory.GENERAL_EPISTLES,
            summary = "Urges believers to contend earnestly for the faith once for all delivered to the saints, closing with one of Scripture's most majestic doxologies.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF211638),
                skyMiddle = Color(0xFF482E75),
                skyHorizon = Color(0xFFEB9B42),
                primaryJewel = Color(0xFF8848C9),
                secondaryJewel = Color(0xFFD94E4E),
                goldLeaf = Color(0xFFFFD466),
                foregroundSilhouette = Color(0xFF1A112B),
                waterOrGround = Color(0xFF322052)
            ),
            defaultArchetype = SceneArchetype.ARMOR_OF_FAITH
        ),

        // ==================== APOCALYPTIC PROPHECY ====================
        BookInfo(
            id = "REV",
            apiName = "Revelation",
            name = "Revelation",
            shortName = "Rev",
            greekTitle = "Apokalypsis Ioannou",
            author = "John the Apostle on Patmos",
            dateWritten = "c. AD 95",
            chapterCount = 22,
            category = TestamentCategory.PROPHECY,
            summary = "The grand apocalyptic unveiling of the risen Christ among the golden lampstands, the cosmic triumph of the Lamb, and the New Jerusalem descending in eternal light.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF120C2B),
                skyMiddle = Color(0xFF351D6B),
                skyHorizon = Color(0xFFF5B841),
                primaryJewel = Color(0xFF9E47E0),
                secondaryJewel = Color(0xFFE03E62),
                goldLeaf = Color(0xFFFFDF73),
                foregroundSilhouette = Color(0xFF0E0921),
                waterOrGround = Color(0xFF1E295E)
            ),
            defaultArchetype = SceneArchetype.NEW_JERUSALEM_TREE
        ),

        // ==================== PSALMS & PROVERBS (POETRY & WISDOM) ====================
        BookInfo(
            id = "PSA",
            apiName = "Psalms",
            name = "Psalms",
            shortName = "Ps",
            greekTitle = "Psalmoi • Tehillim",
            author = "King David, Asaph & the Sons of Korah",
            dateWritten = "c. 1000–450 BC",
            chapterCount = 150,
            category = TestamentCategory.POETRY_AND_WISDOM,
            summary = "The sacred hymnal of Israel and the Church—150 inspired songs of praise, shepherd comfort (Psalm 23), Messianic prophecy, repentance, and Hallelujah symphonies upon the golden harp of Zion.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF0D243B),
                skyMiddle = Color(0xFF1D5B6E),
                skyHorizon = Color(0xFFF5C252),
                primaryJewel = Color(0xFF289E7A),
                secondaryJewel = Color(0xFFD94A64),
                goldLeaf = Color(0xFFFFDE73),
                foregroundSilhouette = Color(0xFF102129),
                waterOrGround = Color(0xFF164A59)
            ),
            defaultArchetype = SceneArchetype.DAVID_HARP_PASTURES
        ),
        BookInfo(
            id = "PRO",
            apiName = "Proverbs",
            name = "Proverbs",
            shortName = "Prov",
            greekTitle = "Paroimiai • Mishlei",
            author = "King Solomon, Agur & King Lemuel",
            dateWritten = "c. 970–700 BC",
            chapterCount = 31,
            category = TestamentCategory.POETRY_AND_WISDOM,
            summary = "Thirty-one chapters of royal Solomonic wisdom—revealing that the fear of the Lord is the beginning of knowledge, the seven pillars of Lady Wisdom, and the noble portrait of Valor in Chapter 31.",
            basePalette = ArtPalette(
                skyTop = Color(0xFF241638),
                skyMiddle = Color(0xFF542E73),
                skyHorizon = Color(0xFFF7BE4D),
                primaryJewel = Color(0xFFC94068),
                secondaryJewel = Color(0xFF3882C4),
                goldLeaf = Color(0xFFFFDF75),
                foregroundSilhouette = Color(0xFF1A1029),
                waterOrGround = Color(0xFF331F4D)
            ),
            defaultArchetype = SceneArchetype.SOLOMON_WISDOM_PILLARS
        )
    )

    val totalNewTestamentChapters: Int = allBooks.sumOf { it.chapterCount } // 441 chapters (260 NT + 150 Psalms + 31 Proverbs)

    fun getBookById(bookId: String): BookInfo {
        return allBooks.firstOrNull { it.id == bookId } ?: allBooks.first()
    }
}

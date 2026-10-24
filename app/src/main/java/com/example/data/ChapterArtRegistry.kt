package com.example.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.example.model.ArtPalette
import com.example.model.BookInfo
import com.example.model.ChapterArtMetadata
import com.example.model.SceneArchetype

object ChapterArtRegistry {

    private data class ChapterSeed(
        val title: String,
        val subtitle: String,
        val archetype: SceneArchetype,
        val themeTag: String,
        val keyVerse: String,
        val meditation: String
    )

    // Curated chapter-specific entries across all 27 books
    private val curatedChapters: Map<String, ChapterSeed> = mapOf(
        // MATTHEW (28)
        "MAT_1" to ChapterSeed("The Royal Genealogy & Nativity", "Lineage of David & Star of Promise", SceneArchetype.NATIVITY_STAR, "Incarnation", "She shall bring forth a son, and you shall call his name JESUS.", "Golden ancestral branches converge beneath the blazing eight-pointed Star of Bethlehem over royal crimson and lapis skies."),
        "MAT_2" to ChapterSeed("The Magi & Star in the East", "Gifts of Gold, Frankincense & Myrrh", SceneArchetype.NATIVITY_STAR, "Epiphany", "When they saw the star, they rejoiced with exceedingly great joy.", "Radiant desert starlight guides the eastern sages toward the humble Judean hills in deep sapphire and warm amber gold."),
        "MAT_3" to ChapterSeed("The Baptism in the Jordan", "Heavens Opened & The Spirit's Dove", SceneArchetype.JORDAN_BAPTISM, "Baptism", "This is my beloved Son, in whom I am well pleased.", "Parted heavens pour golden light upon the turquoise Jordan river as the white dove descends in sacred symmetry."),
        "MAT_4" to ChapterSeed("Temptation & Galilean Dawn", "Light Dawning in Zebulun and Naphtali", SceneArchetype.SEA_OF_GALILEE_BOAT, "Calling", "Follow me, and I will make you fishers of men.", "Morning sun breaks across the Sea of Galilee as fishing nets are cast aside at the Master's call."),
        "MAT_5" to ChapterSeed("The Beatitudes on the Mount", "City on a Hill & Salt of the Earth", SceneArchetype.SERMON_MOUNT, "Beatitudes", "Blessed are the pure in heart, for they shall see God.", "Sunlit olive groves crown the Galilean hillside while nine golden rays symbolize the blessings of the Kingdom."),
        "MAT_6" to ChapterSeed("The Lord's Prayer & Lilies of the Field", "Treasures in Heaven & Fatherly Care", SceneArchetype.SERMON_MOUNT, "Prayer", "Seek first God's Kingdom and his righteousness.", "Wild Galilean lilies and birds of the air rest beneath a warm golden canopy of the Father's providence."),
        "MAT_7" to ChapterSeed("The Golden Rule & House on the Rock", "The Narrow Gate & Unshakable Foundation", SceneArchetype.SERMON_MOUNT, "Wisdom", "Ask, and it will be given you; seek, and you will find.", "A steadfast stone sanctuary stands unshaken above swirling waters beneath a luminous archway."),
        "MAT_8" to ChapterSeed("Calming the Storm on Galilee", "Compassion & Sovereign Authority", SceneArchetype.SEA_OF_GALILEE_BOAT, "Miracles", "Even the winds and the sea obey him!", "Towering sapphire waves bow before a radiant golden horizon as the Galilean boat rests in sudden peace."),
        "MAT_9" to ChapterSeed("The Lord of the Harvest", "Healing, Mercy & New Wineskins", SceneArchetype.GOOD_SHEPHERD, "Mercy", "The harvest indeed is plentiful, but the laborers are few.", "Golden grain fields stretch toward distant hills under a compassionate amber twilight."),
        "MAT_10" to ChapterSeed("Sending the Twelve Apostles", "Sheep Among Wolves & Father's Sparrow", SceneArchetype.SCROLL_AND_LAMP, "Mission", "Freely you received, so freely give.", "Twelve glowing lamps illuminate stone pathways radiating outward across the land."),
        "MAT_11" to ChapterSeed("Rest for the Weary Soul", "The Gentle Yoke of Christ", SceneArchetype.GOOD_SHEPHERD, "Grace", "Come to me, all you who labor and are heavily burdened, and I will give you rest.", "Quiet emerald pastures and still waters reflect the gentle warmth of late afternoon sunlight."),
        "MAT_12" to ChapterSeed("Lord of the Sabbath", "The Bruised Reed & Smoking Flax", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Sabbath", "A bruised reed he will not break, and smoking flax he will not quench.", "A delicate oil wick glows steadily beside classical stone columns bathed in golden light."),
        "MAT_13" to ChapterSeed("Parables of the Kingdom", "The Sower, Pearl of Great Price & Mustard Seed", SceneArchetype.SERMON_MOUNT, "Parables", "The righteous will shine forth like the sun in the Kingdom of their Father.", "Fertile terraced hills and a flourishing tree shelter birds beneath a sky of stained-glass gold."),
        "MAT_14" to ChapterSeed("Five Loaves & Walking on the Sea", "Abundance in the Wilderness & Courage on the Waves", SceneArchetype.LOAVES_AND_FISHES, "Miracles", "Take courage! It is I. Don't be afraid.", "Woven baskets of barley loaves rest by the shore while a luminous figure walks across midnight waves."),
        "MAT_15" to ChapterSeed("Crumbs of Mercy & Four Thousand Fed", "Great Faith & Healing on the Mountain", SceneArchetype.LOAVES_AND_FISHES, "Compassion", "O woman, great is your faith! Be it done to you even as you desire.", "Abundant loaves and fishes shimmer in warm terracotta and turquoise beside the Galilean sea."),
        "MAT_16" to ChapterSeed("The Rock of Confession", "Keys of the Kingdom at Caesarea Philippi", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Confession", "You are the Christ, the Son of the living God.", "Massive living rock rises beneath a celestial arch emblazoned with golden keys."),
        "MAT_17" to ChapterSeed("The Transfiguration in Glory", "Face Shining Like the Sun on the High Mountain", SceneArchetype.TRANSFIGURATION, "Glory", "His face shone like the sun, and his garments became as white as the light.", "Blinding white-gold radiance erupts from the mountain summit amidst swirling sapphire clouds."),
        "MAT_18" to ChapterSeed("The Ninety-Nine & The Lost Sheep", "Childlike Humility & Seventy Times Seven", SceneArchetype.GOOD_SHEPHERD, "Forgiveness", "For where two or three are gathered together in my name, there I am in the middle of them.", "A shepherd's crook and sunlit highland path celebrate the joy of finding the one lost sheep."),
        "MAT_19" to ChapterSeed("With God All Things Are Possible", "Blessing the Children & Eternal Inheritance", SceneArchetype.GOOD_SHEPHERD, "Kingdom", "With men this is impossible, but with God all things are possible.", "Warm golden arches welcome the humble into an abundant garden of grace."),
        "MAT_20" to ChapterSeed("Laborers in the Vineyard", "The Son of Man Came to Serve", SceneArchetype.JERUSALEM_PALMS, "Service", "The Son of Man did not come to be served, but to serve.", "Sun-drenched vineyard terraces lead upward toward the gates of Jerusalem."),
        "MAT_21" to ChapterSeed("Triumphal Entry into Jerusalem", "Hosanna to the Son of David", SceneArchetype.JERUSALEM_PALMS, "Palm Sunday", "Hosanna to the Son of David! Blessed is he who comes in the name of the Lord!", "Waving emerald palm fronds frame the golden gates and stone arches of the Holy City."),
        "MAT_22" to ChapterSeed("The Royal Wedding Banquet", "The Greatest Commandment of Love", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Love", "You shall love the Lord your God with all your heart, with all your soul, and with all your mind.", "Festival lamps illuminate a grand columned hall prepared for the King's wedding feast."),
        "MAT_23" to ChapterSeed("Lament over Jerusalem", "Humility Before the Exalted One", SceneArchetype.JERUSALEM_PALMS, "Humility", "Whoever humbles himself will be exalted.", "Twilight settles over the domes of Jerusalem beneath a protective golden wing."),
        "MAT_24" to ChapterSeed("The Olivet Discourse", "Signs of the Son of Man in Heaven", SceneArchetype.ASCENSION_CLOUDS, "Prophecy", "Heaven and earth will pass away, but my words will not pass away.", "Cosmic lightning and golden clouds herald the coming of the Son of Man above the Mount of Olives."),
        "MAT_25" to ChapterSeed("The Ten Virgins & The Sheep and Goats", "Trimmed Lamps & Faithful Stewardship", SceneArchetype.SCROLL_AND_LAMP, "Watchfulness", "Well done, good and faithful servant. Enter into the joy of your lord.", "Blazing terracotta oil lamps shine through the midnight watch before the bridegroom's door."),
        "MAT_26" to ChapterSeed("The Last Supper & Gethsemane", "Cup of the New Covenant & Midnight Prayer", SceneArchetype.LAST_SUPPER_CHALICE, "Covenant", "This is my blood of the new covenant, which is poured out for many for the remission of sins.", "A golden chalice and broken bread glow softly beside the moonlit olive trees of Gethsemane."),
        "MAT_27" to ChapterSeed("Calvary & The Torn Veil", "The Cross at Golgotha", SceneArchetype.CALVARY_DAWN, "Crucifixion", "Truly this was the Son of God!", "Three crosses stand silhouetted against a dramatic crimson and violet sky as dawn breaks."),
        "MAT_28" to ChapterSeed("The Empty Tomb & Great Commission", "He Is Risen & With You Always", SceneArchetype.EMPTY_TOMB_SUNRISE, "Resurrection", "Behold, I am with you always, even to the end of the age.", "The heavy stone is rolled away as triumphant resurrection gold floods the garden tomb."),

        // MARK (16)
        "MRK_1" to ChapterSeed("The Voice in the Wilderness", "Baptism, Healing & Galilean Dawn", SceneArchetype.JORDAN_BAPTISM, "Beginning", "The time is fulfilled, and God's Kingdom is at hand! Repent, and believe in the Good News.", "Living waters of the Jordan sparkle beneath the descending dove and morning light."),
        "MRK_2" to ChapterSeed("Forgiveness & New Wine", "Lowered Through the Roof in Capernaum", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Faith", "The Sabbath was made for man, not man for the Sabbath.", "Warm sunlight streams through an open rooftop into a crowded Galilean courtyard."),
        "MRK_4" to ChapterSeed("Peace, Be Still!", "Parable of the Sower & Stilling the Tempest", SceneArchetype.SEA_OF_GALILEE_BOAT, "Authority", "Peace! Be still! The wind ceased, and there was a great calm.", "A wooden boat sails into mirror-calm turquoise waters as dark storm clouds part."),
        "MRK_5" to ChapterSeed("Talitha Cumi — Arise!", "Healing the Hemorrhaging Woman & Jairus' Daughter", SceneArchetype.GOOD_SHEPHERD, "Healing", "Daughter, your faith has made you well. Go in peace.", "Golden rays of life and restoration touch a quiet home beside the lake."),
        "MRK_6" to ChapterSeed("Five Thousand Fed & Walking on Water", "Compassion for Sheep Without a Shepherd", SceneArchetype.LOAVES_AND_FISHES, "Provision", "He had compassion on them, because they were like sheep without a shepherd.", "Green hillside grass holds baskets of bread overlooking the shimmering sea."),
        "MRK_8" to ChapterSeed("Taking Up the Cross", "Sight Restored at Bethsaida", SceneArchetype.SERMON_MOUNT, "Discipleship", "For what does it profit a man, to gain the whole world, and forfeit his life?", "Clear mountain light reveals the winding path of discipleship toward the summit."),
        "MRK_9" to ChapterSeed("Transfiguration & Help My Unbelief", "Radiant Garments on the Mountain", SceneArchetype.TRANSFIGURATION, "Glory", "I believe! Help my unbelief!", "Celestial white-gold glory illuminates the mountain peaks above the valley."),
        "MRK_10" to ChapterSeed("Bartimaeus Receives Sight & The Servant King", "A Ransom for Many", SceneArchetype.JERUSALEM_PALMS, "Ransom", "For even the Son of Man came not to be served, but to serve, and to give his life as a ransom for many.", "The dusty road from Jericho opens into dazzling golden light before Jerusalem."),
        "MRK_14" to ChapterSeed("Alabaster Jar & The Upper Room", "Anointed at Bethany & Gethsemane Watch", SceneArchetype.GETHSEMANE_OLIVES, "Devotion", "She has done what she could.", "Fragrant nard and silver moonlight mingle beneath ancient gnarled olive boughs."),
        "MRK_15" to ChapterSeed("The Cross & The Centurion's Cry", "Golgotha at the Ninth Hour", SceneArchetype.CALVARY_DAWN, "Sacrifice", "Truly this man was the Son of God!", "Deep crimson and royal purple skies part with a single beam of golden grace over Calvary."),
        "MRK_16" to ChapterSeed("Risen at Early Dawn", "The Stone Rolled Away & Ascension", SceneArchetype.EMPTY_TOMB_SUNRISE, "Resurrection", "Don't be amazed. You seek Jesus, the Nazarene, who has been crucified. He has risen!", "Morning spices and lilies greet the blazing light of the open garden sepulcher."),

        // LUKE (24)
        "LUK_1" to ChapterSeed("The Magnificat & Benedictus", "Gabriel's Annunciation & Dawn from on High", SceneArchetype.NATIVITY_STAR, "Annunciation", "For nothing is impossible with God.", "Wait of centuries breaks into song as the Dayspring from on high illuminates Nazareth."),
        "LUK_2" to ChapterSeed("Glory to God in the Highest", "The Manger in Bethlehem & Simeon's Song", SceneArchetype.NATIVITY_STAR, "Nativity", "For there is born to you today, in David's city, a Savior, who is Christ the Lord.", "Heavenly choirs of golden light shine over Bethlehem's shepherds and olive hills."),
        "LUK_4" to ChapterSeed("The Spirit of the Lord Is Upon Me", "The Nazareth Scroll & Year of Jubilee", SceneArchetype.SCROLL_AND_LAMP, "Anointing", "The Spirit of the Lord is on me, because he has anointed me to preach good news to the poor.", "An unrolled Isaiah scroll glows beside a synagogue lamp with freedom and healing."),
        "LUK_5" to ChapterSeed("Launch Out into the Deep", "The Miraculous Catch of Fish", SceneArchetype.SEA_OF_GALILEE_BOAT, "Abundance", "Put out into the deep, and let down your nets for a catch.", "Sunlight dances on deep sapphire waters as nets overflow beside two wooden boats."),
        "LUK_10" to ChapterSeed("The Good Samaritan & Mary at Jesus' Feet", "Mercy on the Jericho Road & The One Thing Needed", SceneArchetype.GOOD_SHEPHERD, "Compassion", "Go and do likewise.", "A winding sunlit road through terraced hills leads to a welcoming inn of mercy."),
        "LUK_15" to ChapterSeed("The Lost Sheep, Lost Coin & Prodigal Son", "The Father's Embrace & Joy in Heaven", SceneArchetype.GOOD_SHEPHERD, "Grace", "For this, my son, was dead, and is alive again; he was lost, and is found!", "A warm golden road leads homeward to open archways and a festive banquet of grace."),
        "LUK_19" to ChapterSeed("Zacchaeus & The Stones Cry Out", "Salvation Comes to Jericho", SceneArchetype.JERUSALEM_PALMS, "Salvation", "For the Son of Man came to seek and to save that which was lost.", "A spreading sycamore-fig tree and waving palms frame the approach to Jerusalem."),
        "LUK_22" to ChapterSeed("Remember Me & The Mount of Olives", "The New Covenant Cup & Angelic Strength", SceneArchetype.LAST_SUPPER_CHALICE, "Covenant", "Do this in memory of me.", "Candlelit gold warms the Upper Room chalice before the silver moonlit garden of prayer."),
        "LUK_23" to ChapterSeed("Today You Will Be with Me in Paradise", "Father, Forgive Them", SceneArchetype.CALVARY_DAWN, "Forgiveness", "Father, forgive them, for they don't know what they are doing.", "Rays of pardoning light pierce the darkness above the three crosses of Calvary."),
        "LUK_24" to ChapterSeed("The Road to Emmaus & Burning Hearts", "Known in the Breaking of Bread", SceneArchetype.EMPTY_TOMB_SUNRISE, "Resurrection", "Wasn't our heart burning within us, while he spoke to us on the way?", "Two travelers walk a golden sunset path toward Emmaus where broken bread radiates light."),

        // JOHN (21)
        "JHN_1" to ChapterSeed("In the Beginning Was the Word", "The Light Shines in the Darkness", SceneArchetype.NATIVITY_STAR, "Incarnation", "The Word became flesh, and lived among us. We saw his glory.", "Cosmic sapphire darkness parts before the primordial golden Logos star and dove of grace."),
        "JHN_2" to ChapterSeed("Water Turned to Wine at Cana", "The First Sign of Glory", SceneArchetype.LAST_SUPPER_CHALICE, "Miracles", "This beginning of his signs Jesus did in Cana of Galilee, and revealed his glory.", "Six stone waterjars and a ruby chalice shimmer with festive golden light."),
        "JHN_3" to ChapterSeed("Born of Water and Spirit", "God So Loved the World", SceneArchetype.JORDAN_BAPTISM, "Eternal Life", "For God so loved the world, that he gave his one and only Son.", "A gentle night breeze and luminous heavenly light embrace the world in sapphire and gold."),
        "JHN_4" to ChapterSeed("Living Water at Jacob's Well", "Worship in Spirit and Truth", SceneArchetype.JORDAN_BAPTISM, "Living Water", "The water that I will give him will become in him a well of water springing up to eternal life.", "Crystal living water overflows from an ancient stone well beneath white Samaritan harvest fields."),
        "JHN_6" to ChapterSeed("I Am the Bread of Life", "Five Barley Loaves & Words of Eternal Life", SceneArchetype.LOAVES_AND_FISHES, "Bread of Life", "I am the bread of life. He who comes to me will not be hungry.", "Golden barley loaves radiate heavenly manna light beside the Sea of Tiberias."),
        "JHN_8" to ChapterSeed("I Am the Light of the World", "The Truth Shall Make You Free", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Light", "I am the light of the world. He who follows me will not walk in the darkness.", "Towering temple festival candelabra blaze against the dawn sky over Jerusalem."),
        "JHN_10" to ChapterSeed("I Am the Good Shepherd", "Abundant Life & The Shepherd's Voice", SceneArchetype.GOOD_SHEPHERD, "Shepherd", "I am the good shepherd. The good shepherd lays down his life for the sheep.", "Lush emerald hills and a stone sheepfold gate glow under a protective golden sun."),
        "JHN_11" to ChapterSeed("I Am the Resurrection and the Life", "Lazarus Come Forth at Bethany", SceneArchetype.EMPTY_TOMB_SUNRISE, "Resurrection", "I am the resurrection and the life. He who believes in me will still live, even if he dies.", "Life-giving light pours into the rock tomb of Bethany as mourning turns to joy."),
        "JHN_13" to ChapterSeed("Love One Another", "Washing the Disciples' Feet in the Upper Room", SceneArchetype.LAST_SUPPER_CHALICE, "Love", "By this everyone will know that you are my disciples, if you have love for one another.", "A basin of water, linen towel, and glowing lamp illuminate the humility of divine love."),
        "JHN_14" to ChapterSeed("The Way, the Truth, and the Life", "The Father's House & Promise of the Comforter", SceneArchetype.THRONE_AND_RAINBOW, "Peace", "Peace I leave with you. My peace I give to you.", "Luminous mansions of golden light and a descending dove bring serene peace to troubled hearts."),
        "JHN_15" to ChapterSeed("I Am the True Vine", "Abiding in Love & Bearing Much Fruit", SceneArchetype.GETHSEMANE_OLIVES, "Abiding", "I am the vine. You are the branches. He who remains in me and I in him bears much fruit.", "Lush purple grape clusters and golden vine branches intertwine across a sunlit trellis."),
        "JHN_17" to ChapterSeed("The High Priestly Prayer", "That They May All Be One", SceneArchetype.ASCENSION_CLOUDS, "Unity", "Sanctify them in your truth; your word is truth.", "Eyes lifted to a starry indigo heaven where golden bands of unity encircle the world."),
        "JHN_19" to ChapterSeed("It Is Finished!", "The Crown of Thorns & Pierced Side", SceneArchetype.CALVARY_DAWN, "Redemption", "When Jesus therefore had received the vinegar, he said, 'It is finished!'", "Ruby and crystal streams flow beneath the cross as resurrection dawn touches the horizon."),
        "JHN_20" to ChapterSeed("Mary in the Garden & Peace Be with You", "My Lord and My God!", SceneArchetype.EMPTY_TOMB_SUNRISE, "Faith", "Blessed are those who have not seen, and have believed.", "Morning dew on garden lilies reflects the risen Lord in radiant white and gold."),
        "JHN_21" to ChapterSeed("Breakfast by the Sea of Tiberias", "Feed My Sheep", SceneArchetype.SEA_OF_GALILEE_BOAT, "Restoration", "Lord, you know everything; you know that I love you.", "A glowing charcoal fire on the beach welcomes the fishing boat across turquoise dawn waters."),

        // ACTS (28)
        "ACT_1" to ChapterSeed("The Ascension from Mount Olivet", "Witnesses to the Ends of the Earth", SceneArchetype.ASCENSION_CLOUDS, "Ascension", "You will receive power when the Holy Spirit has come upon you.", "Golden clouds lift above the olive groves of Jerusalem into a radiant sapphire dome."),
        "ACT_2" to ChapterSeed("The Day of Pentecost", "Tongues of Fire & Three Thousand Souls", SceneArchetype.PENTECOST_FLAMES, "Pentecost", "They were all filled with the Holy Spirit.", "Twelve flames of sacred amber and crimson descend with the dove into the columned Upper Room."),
        "ACT_3" to ChapterSeed("Healing at the Beautiful Gate", "Walking, Leaping & Praising God", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Miracles", "Silver and gold have I none, but what I have, that I give you. In the name of Jesus Christ of Nazareth, get up and walk!", "Ornate bronze and marble columns of the Beautiful Gate shimmer in morning sunlight."),
        "ACT_7" to ChapterSeed("Stephen's Vision of Heaven Opened", "The Son of Man Standing at God's Right Hand", SceneArchetype.THRONE_AND_RAINBOW, "Martyrdom", "Behold, I see the heavens opened, and the Son of Man standing at the right hand of God!", "A shaft of celestial glory pierces the sky above Jerusalem's walls."),
        "ACT_9" to ChapterSeed("Blinding Light on the Damascus Road", "Saul Chosen as a Vessel of Grace", SceneArchetype.DAMASCUS_LIGHT, "Conversion", "Suddenly a light from heaven shone around him.", "Blinding golden-white radiance strikes the desert road to Damascus amidst palm silhouettes."),
        "ACT_10" to ChapterSeed("Cornelius' Vision & Gentiles Filled", "God Shows No Partiality", SceneArchetype.PENTECOST_FLAMES, "Inclusion", "Of a truth I perceive that God doesn't show favoritism.", "Byzantine light streams over the Mediterranean coast at Caesarea."),
        "ACT_12" to ChapterSeed("Peter Delivered by the Angel", "Chains Fall Off in the Night", SceneArchetype.DAMASCUS_LIGHT, "Deliverance", "A light shone in the cell, and struck Peter on the side, and woke him up.", "Angelic light shatters iron prison shadows and opens the iron gate to the city."),
        "ACT_16" to ChapterSeed("Hymns at Midnight in Philippi", "Lydia's Open Heart & The Jailer's Household", SceneArchetype.AREOPAGUS_ATHENS, "Praise", "Believe in the Lord Jesus Christ, and you will be saved, you and your household.", "Midnight songs of praise shake stone foundations as golden light fills the Philippian prison."),
        "ACT_17" to ChapterSeed("Paul on the Areopagus in Athens", "Proclaiming the Unknown God on Mars Hill", SceneArchetype.AREOPAGUS_ATHENS, "Wisdom", "For in him we live, move, and have our being.", "Classical marble columns of Athens stand against a star-filled violet twilight sky."),
        "ACT_27" to ChapterSeed("The Tempest & Shipwreck at Malta", "Not a Hair Shall Perish", SceneArchetype.MEDITERRANEAN_VOYAGE, "Providence", "Cheer up, men! For I believe God, that it will be just as it was spoken to me.", "A storm-tossed grain ship with glowing golden anchor approaches the shores of Malta."),
        "ACT_28" to ChapterSeed("The Gospel Unhindered in Rome", "Preaching the Kingdom with All Boldness", SceneArchetype.SCROLL_AND_LAMP, "Triumph", "Preaching the Kingdom of God, and teaching the things concerning the Lord Jesus Christ with all boldness, without hindrance.", "An unrolled scroll and blazing lamp shine beside Roman arches at the heart of the Empire."),

        // ROMANS TO JUDE HIGHLIGHTS
        "ROM_1" to ChapterSeed("The Power of God for Salvation", "The Righteous Shall Live by Faith", SceneArchetype.SCROLL_AND_LAMP, "Gospel", "For I am not ashamed of the Good News of Christ, because it is the power of God for salvation.", "A radiant golden scroll illuminates classical arches with the light of faith."),
        "ROM_5" to ChapterSeed("Peace with God & Abounding Grace", "Hope That Does Not Disappoint", SceneArchetype.ANCHOR_OF_HOPE, "Grace", "God commends his own love toward us, in that while we were yet sinners, Christ died for us.", "A ruby cross and golden anchor shine beneath an overflowing fountain of grace."),
        "ROM_8" to ChapterSeed("More Than Conquerors", "Nothing Can Separate Us from the Love of God", SceneArchetype.THRONE_AND_RAINBOW, "Victory", "Nothing will be able to separate us from the love of God, which is in Christ Jesus our Lord.", "Cosmic stars and a triumphant golden crown blaze above every height and depth."),
        "ROM_12" to ChapterSeed("Living Sacrifices & Transformed Minds", "Overcoming Evil with Good", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Consecration", "Be transformed by the renewing of your mind.", "Sacred altar flames rise in golden symmetry within a sanctuary of peace."),
        "1CO_13" to ChapterSeed("The Supremacy of Divine Love", "Faith, Hope, and Love — The Greatest Is Love", SceneArchetype.LAST_SUPPER_CHALICE, "Love", "Love never fails. But now faith, hope, and love remain—these three. The greatest of these is love.", "Three intertwined stained-glass medallions of ruby, emerald, and gold radiate eternal warmth."),
        "1CO_15" to ChapterSeed("The Glorious Resurrection Body", "O Death, Where Is Your Victory?", SceneArchetype.EMPTY_TOMB_SUNRISE, "Resurrection", "Thanks be to God, who gives us the victory through our Lord Jesus Christ.", "A planted seed bursts into a radiant golden lily beneath the trumpet of resurrection dawn."),
        "2CO_4" to ChapterSeed("Treasure in Jars of Clay", "Light Shining Out of Darkness", SceneArchetype.SCROLL_AND_LAMP, "Glory", "We have this treasure in clay vessels, that the exceeding greatness of the power may be of God.", "Warm celestial light pours through cracks in a terracotta vessel against a sapphire night."),
        "2CO_5" to ChapterSeed("A New Creation in Christ", "Ambassadors of Reconciliation", SceneArchetype.JORDAN_BAPTISM, "New Creation", "If anyone is in Christ, he is a new creation. The old things have passed away; behold, all things have become new.", "Dawn light transforms a shadowed valley into a flourishing emerald garden."),
        "GAL_5" to ChapterSeed("The Fruit of the Spirit", "Walking in Liberty & Love", SceneArchetype.NEW_JERUSALEM_TREE, "Spirit", "The fruit of the Spirit is love, joy, peace, patience, kindness, goodness, faithfulness, gentleness, and self-control.", "A flourishing tree by living water bears nine luminous golden fruits."),
        "EPH_2" to ChapterSeed("Saved by Grace & Christ Our Cornerstone", "One New Humanity in the Temple of God", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Grace", "For by grace you have been saved through faith, and that not of yourselves; it is the gift of God.", "A radiant golden cornerstone unites majestic marble arches into a holy temple."),
        "EPH_6" to ChapterSeed("The Whole Armor of God", "Standing Firm in the Lord's Mighty Power", SceneArchetype.ARMOR_OF_FAITH, "Spiritual Warfare", "Put on the whole armor of God, that you may be able to stand.", "A gilded shield of faith, helmet of salvation, and sword of light gleam beneath cathedral glass."),
        "PHP_2" to ChapterSeed("The Carmen Christi — Every Knee Shall Bow", "From Servant Humility to the Highest Name", SceneArchetype.THRONE_AND_RAINBOW, "Exaltation", "That at the name of Jesus every knee should bow, of those in heaven, those on earth, and those under the earth.", "A path descending into humble shadow rises into a blazing cosmic throne of gold."),
        "PHP_4" to ChapterSeed("The Peace of God & Strength in All Things", "Rejoice in the Lord Always", SceneArchetype.SCROLL_AND_LAMP, "Peace", "I can do all things through Christ, who strengthens me.", "A serene lamp and olive wreath glow in tranquil purple and gold."),
        "COL_1" to ChapterSeed("The Supremacy of the Firstborn", "Image of the Invisible God", SceneArchetype.THRONE_AND_RAINBOW, "Supremacy", "He is before all things, and in him all things are held together.", "Galaxies and stained-glass spheres revolve around a central golden cross of light."),
        "2TI_4" to ChapterSeed("The Good Fight & Crown of Righteousness", "The Word Preached in Season and Out of Season", SceneArchetype.ARMOR_OF_FAITH, "Endurance", "I have fought the good fight. I have finished the course. I have kept the faith.", "A golden victor's laurel crown shines above a finish line bathed in sunrise."),
        "HEB_1" to ChapterSeed("The Radiance of God's Glory", "God Has Spoken by His Son", SceneArchetype.THRONE_AND_RAINBOW, "Majesty", "His Son is the radiance of his glory, the very image of his substance.", "Pure golden light radiates from the eternal scepter and throne of heaven."),
        "HEB_4" to ChapterSeed("The Living Word & Throne of Grace", "Approaching with Boldness for Mercy", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Grace", "Let's therefore draw near with boldness to the throne of grace.", "Parted sanctuary curtains reveal a welcoming throne bathed in emerald and gold."),
        "HEB_6" to ChapterSeed("An Anchor of the Soul, Sure and Steadfast", "Entering Within the Veil", SceneArchetype.ANCHOR_OF_HOPE, "Hope", "This hope we have as an anchor of the soul, a hope both sure and steadfast.", "A gilded anchor forged in heaven rests securely beneath sapphire waves."),
        "HEB_11" to ChapterSeed("The Great Hall of Faith", "Looking to the City with Foundations", SceneArchetype.ANCHOR_OF_HOPE, "Faith", "Now faith is assurance of things hoped for, proof of things not seen.", "Starry constellations over desert tents point toward a gleaming heavenly city."),
        "HEB_12" to ChapterSeed("Cloud of Witnesses & Mount Zion", "Looking to Jesus, the Author and Finisher of Faith", SceneArchetype.ASCENSION_CLOUDS, "Perseverance", "Looking to Jesus, the author and perfecter of faith.", "A luminous amphitheater of golden clouds surrounds the ascending path to Mount Zion."),
        "JAS_1" to ChapterSeed("Wisdom from Above & Crown of Life", "Every Good and Perfect Gift", SceneArchetype.SERMON_MOUNT, "Wisdom", "Every good gift and every perfect gift is from above, coming down from the Father of lights.", "Warm sunbeams without shadow stream down upon green hills and blooming flowers."),
        "1PE_1" to ChapterSeed("A Living Hope & Imperishable Inheritance", "Faith Refined Like Gold by Fire", SceneArchetype.ANCHOR_OF_HOPE, "Living Hope", "According to his great mercy, he caused us to be born again to a living hope through the resurrection of Jesus Christ.", "Molten gold in a crucible reflects the sunrise of an imperishable crown."),
        "1PE_2" to ChapterSeed("Living Stones & A Royal Priesthood", "Called Out of Darkness into Marvelous Light", SceneArchetype.TEMPLE_SOLOMON_PORCH, "Identity", "That you may proclaim the excellence of him who called you out of darkness into his marvelous light.", "Cut jewel stones form a luminous spiritual house around the Chief Cornerstone."),
        "2PE_1" to ChapterSeed("The Morning Star & Majestic Glory", "Eyewitnesses on the Holy Mountain", SceneArchetype.TRANSFIGURATION, "Glory", "Until the day dawns, and the morning star arises in your hearts.", "The bright Morning Star rises over mountain peaks in sapphire and gold."),
        "1JN_1" to ChapterSeed("God Is Light — Walking in the Light", "Fellowship with the Father and the Son", SceneArchetype.DAMASCUS_LIGHT, "Light", "God is light, and in him is no darkness at all.", "Pure prismatic light fills an alabaster sanctuary without a single shadow."),
        "1JN_4" to ChapterSeed("God Is Love — Perfect Love Casts Out Fear", "We Love Because He First Loved Us", SceneArchetype.LAST_SUPPER_CHALICE, "Love", "We love him, because he first loved us.", "Warm ruby and gold stained-glass roses radiate the fullness of divine charity."),
        "JUD_1" to ChapterSeed("Contending for the Faith & Majestic Doxology", "Kept Stumble-Free Before His Glory", SceneArchetype.ARMOR_OF_FAITH, "Doxology", "Now to him who is able to keep you from stumbling, and to present you faultless before the presence of his glory in great joy.", "A fortress shield and blazing starry doxology crown the epistle."),

        // REVELATION (22)
        "REV_1" to ChapterSeed("The Son of Man Among Seven Golden Lampstands", "Alpha and Omega, First and Last", SceneArchetype.SEVEN_LAMPSTANDS, "Vision", "I am the Alpha and the Omega, says the Lord God, who is and who was and who is to come, the Almighty.", "Seven blazing golden menorah lampstands and seven stars surround a figure of blinding gold."),
        "REV_2" to ChapterSeed("Letters to Ephesus, Smyrna, Pergamum & Thyatira", "Tree of Life, Crown of Life & Morning Star", SceneArchetype.SEVEN_LAMPSTANDS, "Churches", "Be faithful to death, and I will give you the crown of life.", "Four ornate golden lamps shine above ancient Aegean marble cities."),
        "REV_3" to ChapterSeed("Letters to Sardis, Philadelphia & Laodicea", "Behold, I Stand at the Door and Knock", SceneArchetype.SEVEN_LAMPSTANDS, "Churches", "Behold, I stand at the door and knock. If anyone hears my voice and opens the door, then I will come in to him.", "An arched wooden door framed in warm golden lantern light opens to the King."),
        "REV_4" to ChapterSeed("The Throne in Heaven & Emerald Rainbow", "Holy, Holy, Holy Before the Sea of Glass", SceneArchetype.THRONE_AND_RAINBOW, "Worship", "Holy, holy, holy, is the Lord God, the Almighty, who was and who is and who is to come!", "An emerald rainbow arches over a sapphire throne and crystal sea surrounded by twenty-four golden crowns."),
        "REV_5" to ChapterSeed("The Lion of Judah & The Worthy Lamb", "The Seven-Sealed Scroll & New Song", SceneArchetype.THRONE_AND_RAINBOW, "Redemption", "Worthy is the Lamb who has been killed to receive the power, wealth, wisdom, strength, honor, glory, and blessing!", "Golden harps and bowls of incense encircle the Lamb standing in the center of the throne."),
        "REV_7" to ChapterSeed("The Great Multitude in White Robes", "Palm Branches Before the Throne & Living Fountains", SceneArchetype.JERUSALEM_PALMS, "Salvation", "Salvation belongs to our God, who sits on the throne, and to the Lamb!", "Countless waving palms and radiant white robes gather beside fountains of living water."),
        "REV_12" to ChapterSeed("The Woman Clothed with the Sun", "Crown of Twelve Stars & Victory by the Lamb's Blood", SceneArchetype.NATIVITY_STAR, "Cosmic Triumph", "They overcame him because of the Lamb's blood, and because of the word of their testimony.", "A solar halo, crescent moon, and twelve blazing stars illuminate the cosmic heavens."),
        "REV_14" to ChapterSeed("The Lamb on Mount Zion & Eternal Gospel", "The Harvest of the Earth", SceneArchetype.TRANSFIGURATION, "Mount Zion", "Fear God and give him glory; for the hour of his judgment has come.", "The Lamb stands atop golden Mount Zion while angels fly across the mid-heaven."),
        "REV_15" to ChapterSeed("The Sea of Glass Mixed with Fire", "The Song of Moses and the Lamb", SceneArchetype.THRONE_AND_RAINBOW, "Praise", "Great and marvelous are your works, Lord God, the Almighty!", "A shimmering sea of crystal glass mingled with golden fire reflects heavenly harps."),
        "REV_19" to ChapterSeed("Hallelujah! The Marriage Supper of the Lamb", "King of Kings and Lord of Lords", SceneArchetype.THRONE_AND_RAINBOW, "Triumph", "Hallelujah! For the Lord our God, the Almighty, reigns!", "Heavens open in royal crimson and gold for the wedding feast of the King of Kings."),
        "REV_21" to ChapterSeed("The New Heaven & The New Jerusalem", "Twelve Gates of Pearl & No More Tears", SceneArchetype.NEW_JERUSALEM_TREE, "New Creation", "He will wipe away every tear from their eyes. Death will be no more.", "The holy city New Jerusalem descends like a jeweled bride in jasper, sapphire, emerald, and pure gold."),
        "REV_22" to ChapterSeed("The River of Water of Life & Tree of Life", "Come, Lord Jesus!", SceneArchetype.NEW_JERUSALEM_TREE, "Eternity", "The Spirit and the bride say, 'Come!' He who hears, let him say, 'Come!'", "A crystal-clear River of Life flows from the throne between the twelve-fruited Tree of Life in eternal day.")
    )

    private val gospelArchetypeCycle = listOf(
        SceneArchetype.SERMON_MOUNT,
        SceneArchetype.SEA_OF_GALILEE_BOAT,
        SceneArchetype.LOAVES_AND_FISHES,
        SceneArchetype.GOOD_SHEPHERD,
        SceneArchetype.TEMPLE_SOLOMON_PORCH,
        SceneArchetype.JORDAN_BAPTISM,
        SceneArchetype.JERUSALEM_PALMS,
        SceneArchetype.TRANSFIGURATION
    )

    private val actsArchetypeCycle = listOf(
        SceneArchetype.PENTECOST_FLAMES,
        SceneArchetype.TEMPLE_SOLOMON_PORCH,
        SceneArchetype.DAMASCUS_LIGHT,
        SceneArchetype.MEDITERRANEAN_VOYAGE,
        SceneArchetype.AREOPAGUS_ATHENS,
        SceneArchetype.SCROLL_AND_LAMP
    )

    private val epistleArchetypeCycle = listOf(
        SceneArchetype.SCROLL_AND_LAMP,
        SceneArchetype.ARMOR_OF_FAITH,
        SceneArchetype.ANCHOR_OF_HOPE,
        SceneArchetype.TEMPLE_SOLOMON_PORCH,
        SceneArchetype.GOOD_SHEPHERD,
        SceneArchetype.THRONE_AND_RAINBOW
    )

    private val revelationArchetypeCycle = listOf(
        SceneArchetype.SEVEN_LAMPSTANDS,
        SceneArchetype.THRONE_AND_RAINBOW,
        SceneArchetype.ASCENSION_CLOUDS,
        SceneArchetype.NEW_JERUSALEM_TREE,
        SceneArchetype.CALVARY_DAWN
    )

    // Hue shift accents so every chapter within a book gets its own distinct color harmony
    private val chapterHueAccents = listOf(
        Color(0xFFE6A832), // Amber Gold
        Color(0xFF2B8CBE), // Galilean Azure
        Color(0xFFD1495B), // Cana Crimson
        Color(0xFF2E9E75), // Olive Emerald
        Color(0xFF8C52CC), // Royal Amethyst
        Color(0xFFE07A3C), // Dawn Terracotta
        Color(0xFF3B6FD4), // Lapis Lazuli
        Color(0xFFC44569)  // Rose Ruby
    )

    fun getChapterMetadata(bookId: String, chapterNumber: Int): ChapterArtMetadata {
        val book = NewTestamentBooks.getBookById(bookId)
        val key = "${book.id}_$chapterNumber"
        val curated = curatedChapters[key]
        val palette = deriveChapterPalette(book, chapterNumber)
        val patternSeed = (book.id.hashCode() * 31) + (chapterNumber * 97)

        if (curated != null) {
            return ChapterArtMetadata(
                bookId = book.id,
                bookName = book.name,
                chapterNumber = chapterNumber,
                artTitle = curated.title,
                artSubtitle = curated.subtitle,
                sceneArchetype = curated.archetype,
                visualMeditation = curated.meditation,
                keyThemeTag = curated.themeTag,
                keyVersePreview = curated.keyVerse,
                palette = palette,
                patternSeed = patternSeed
            )
        }

        // Dynamic rich metadata for remaining chapters based on book & chapter narrative progression
        val archetype = selectArchetypeForChapter(book, chapterNumber)
        val generatedTitle = generateChapterTitle(book, chapterNumber, archetype)
        val generatedSubtitle = "${book.name} Chapter $chapterNumber • ${archetype.displayName}"
        val generatedMeditation =
            "An illuminated sacred meditation on ${book.name} $chapterNumber, rendered in stained-glass jewel tones and gold leaf around the motif of ${archetype.displayName.lowercase()}."
        val keyVerse = OfflineScriptureProvider.getFirstVersePreview(book.id, chapterNumber)

        return ChapterArtMetadata(
            bookId = book.id,
            bookName = book.name,
            chapterNumber = chapterNumber,
            artTitle = generatedTitle,
            artSubtitle = generatedSubtitle,
            sceneArchetype = archetype,
            visualMeditation = generatedMeditation,
            keyThemeTag = archetype.themeCategory,
            keyVersePreview = keyVerse,
            palette = palette,
            patternSeed = patternSeed
        )
    }

    fun getAllChaptersForBook(bookId: String): List<ChapterArtMetadata> {
        val book = NewTestamentBooks.getBookById(bookId)
        return (1..book.chapterCount).map { ch -> getChapterMetadata(bookId, ch) }
    }

    fun getAll260Chapters(): List<ChapterArtMetadata> {
        return NewTestamentBooks.allBooks.flatMap { book ->
            (1..book.chapterCount).map { ch -> getChapterMetadata(book.id, ch) }
        }
    }

    private fun deriveChapterPalette(book: BookInfo, chapter: Int): ArtPalette {
        val base = book.basePalette
        val accentA = chapterHueAccents[(chapter + book.id.hashCode().coerceAtLeast(0)) % chapterHueAccents.size]
        val accentB = chapterHueAccents[(chapter * 3 + 2) % chapterHueAccents.size]
        val t = ((chapter % 5) + 1) * 0.08f
        return ArtPalette(
            skyTop = lerp(base.skyTop, accentA.copy(alpha = 1f), t * 0.45f),
            skyMiddle = lerp(base.skyMiddle, accentB.copy(alpha = 1f), t * 0.55f),
            skyHorizon = lerp(base.skyHorizon, accentA, t * 0.5f),
            primaryJewel = lerp(base.primaryJewel, accentA, 0.35f),
            secondaryJewel = lerp(base.secondaryJewel, accentB, 0.35f),
            goldLeaf = base.goldLeaf,
            foregroundSilhouette = base.foregroundSilhouette,
            waterOrGround = lerp(base.waterOrGround, accentB, t * 0.4f)
        )
    }

    private fun selectArchetypeForChapter(book: BookInfo, chapter: Int): SceneArchetype {
        return when (book.id) {
            "MAT", "MRK", "LUK", "JHN" -> gospelArchetypeCycle[(chapter + book.id[0].code) % gospelArchetypeCycle.size]
            "ACT" -> actsArchetypeCycle[chapter % actsArchetypeCycle.size]
            "REV" -> revelationArchetypeCycle[chapter % revelationArchetypeCycle.size]
            else -> epistleArchetypeCycle[(chapter + book.id.hashCode().coerceAtLeast(0)) % epistleArchetypeCycle.size]
        }
    }

    private fun generateChapterTitle(book: BookInfo, chapter: Int, archetype: SceneArchetype): String {
        val chapterThemes = when (book.id) {
            "MRK" -> mapOf(
                3 to "The Twelve Appointed & House Divided",
                7 to "Clean Hearts & Ephphatha Healing",
                11 to "Triumphal Entry & House of Prayer",
                12 to "The Cornerstone & Widow's Two Mites",
                13 to "Watchfulness on the Mount of Olives"
            )
            "LUK" -> mapOf(
                3 to "Voice in the Desert & Genealogy of Grace",
                6 to "Sermon on the Plain & Merciful Love",
                7 to "The Centurion's Faith & Alabaster Tears",
                8 to "The Sower, Calmed Tempest & Jairus' Daughter",
                9 to "Five Loaves, Transfiguration & Steadfast Face",
                11 to "Lord, Teach Us to Pray",
                12 to "Ravens, Lilies & Watchful Lamps",
                13 to "The Mustard Tree & Narrow Door",
                14 to "The Great Banquet of Grace",
                16 to "Faithful Stewardship & Eternal Riches",
                17 to "Ten Lepers Cleansed & The Kingdom Within",
                18 to "The Persistent Widow & Humble Publican",
                20 to "The Beloved Son & Cornerstone",
                21 to "The Widow's Offering & Redemption Drawing Near"
            )
            "JHN" -> mapOf(
                5 to "Healing at the Pool of Bethesda",
                7 to "Rivers of Living Water at the Feast",
                9 to "The Man Born Blind Sees the Light",
                12 to "Anointing at Bethany & Grain of Wheat",
                16 to "Sorrow Turned to Joy & The Spirit of Truth",
                18 to "The Garden Arrest & Kingdom of Truth"
            )
            "ACT" -> mapOf(
                4 to "Boldness of Peter and John in Prayer",
                5 to "Apostolic Signs in Solomon's Portico",
                6 to "Seven Chosen Full of the Spirit & Wisdom",
                8 to "Philip & The Ethiopian Chariot Scroll",
                11 to "First Called Christians at Antioch",
                13 to "Sent Forth by the Spirit from Antioch",
                14 to "Light to the Nations in Lystra & Iconium",
                15 to "The Jerusalem Council of Grace",
                18 to "Tentmakers in Corinth & Apollos' Eloquence",
                19 to "Mighty Works of the Word in Ephesus",
                20 to "Breaking Bread at Troas & Farewell at Miletus",
                21 to "Paul's Journey to Jerusalem",
                22 to "Defense on the Temple Steps",
                23 to "The Lord Stands by Paul in the Night",
                24 to "Witness Before Felix at Caesarea",
                25 to "Appeal to Caesar",
                26 to "Not Disobedient to the Heavenly Vision"
            )
            "ROM" -> mapOf(
                2 to "The Law Written on the Heart",
                3 to "Justified Freely by His Grace",
                4 to "Abraham's Faith Against All Hope",
                6 to "Walking in Newness of Life",
                7 to "Delivered Through Jesus Christ Our Lord",
                9 to "The Potter's Vessel of Mercy",
                10 to "How Beautiful Are the Feet of Good News",
                11 to "The Wild Olive Branch Grafted In",
                13 to "Armor of Light & Fulfilling the Law in Love",
                14 to "The Kingdom of Righteousness, Peace & Joy",
                15 to "Abounding in Hope by the Power of the Spirit",
                16 to "Greetings to the Saints in Rome"
            )
            "REV" -> mapOf(
                6 to "Opening of the Six Seals",
                8 to "Golden Censer & Prayers of the Saints",
                9 to "The Trumpets of Warning",
                10 to "The Mighty Angel & Little Scroll",
                11 to "The Kingdom of the World Becomes Our Lord's",
                13 to "Endurance & Faith of the Saints",
                16 to "Golden Bowls of Divine Justice",
                17 to "The Lamb Will Overcome Them",
                18 to "Fall of Babylon & Voice from Heaven",
                20 to "The Thousand Years & Book of Life"
            )
            else -> emptyMap()
        }
        return chapterThemes[chapter] ?: "${book.name} $chapter: ${archetype.displayName}"
    }
}

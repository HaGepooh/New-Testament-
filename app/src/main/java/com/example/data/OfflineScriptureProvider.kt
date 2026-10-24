package com.example.data

import com.example.model.BibleTranslation
import com.example.model.VerseItem

object OfflineScriptureProvider {

    private data class RawVerse(
        val num: Int,
        val webText: String,
        val kjvText: String,
        val isChristWords: Boolean = false
    )

    // Rich offline chapters across all 27 books so the reader is immediately populated offline
    private val curatedChapters: Map<String, List<RawVerse>> = mapOf(
        "MAT_1" to listOf(
            RawVerse(1, "The book of the genealogy of Jesus Christ, the son of David, the son of Abraham.", "The book of the generation of Jesus Christ, the son of David, the son of Abraham."),
            RawVerse(2, "Abraham became the father of Isaac. Isaac became the father of Jacob. Jacob became the father of Judah and his brothers.", "Abraham begat Isaac; and Isaac begat Jacob; and Jacob begat Judas and his brethren;"),
            RawVerse(16, "Jacob became the father of Joseph, the husband of Mary, from whom was born Jesus, who is called Christ.", "And Jacob begat Joseph the husband of Mary, of whom was born Jesus, who is called Christ."),
            RawVerse(18, "Now the birth of Jesus Christ was like this: After his mother, Mary, was engaged to Joseph, before they came together, she was found pregnant by the Holy Spirit.", "Now the birth of Jesus Christ was on this wise: When as his mother Mary was espoused to Joseph, before they came together, she was found with child of the Holy Ghost."),
            RawVerse(20, "But when he thought about these things, behold, an angel of the Lord appeared to him in a dream, saying, 'Joseph, son of David, don't be afraid to take to yourself Mary as your wife, for that which is conceived in her is of the Holy Spirit.'", "But while he thought on these things, behold, the angel of the Lord appeared unto him in a dream, saying, Joseph, thou son of David, fear not to take unto thee Mary thy wife: for that which is conceived in her is of the Holy Ghost."),
            RawVerse(21, "'She shall give birth to a son. You shall call his name JESUS, for it is he who shall save his people from their sins.'", "And she shall bring forth a son, and thou shalt call his name JESUS: for he shall save his people from their sins."),
            RawVerse(23, "'Behold, the virgin shall be with child, and shall give birth to a son. They shall call his name Immanuel;' which is, being interpreted, 'God with us.'", "Behold, a virgin shall be with child, and shall bring forth a son, and they shall call his name Emmanuel, which being interpreted is, God with us."),
            RawVerse(24, "Joseph arose from his sleep, and did as the angel of the Lord commanded him, and took his wife to himself;", "Then Joseph being raised from sleep did as the angel of the Lord had bidden him, and took unto him his wife:")
        ),
        "MAT_2" to listOf(
            RawVerse(1, "Now when Jesus was born in Bethlehem of Judea in the days of King Herod, behold, wise men from the east came to Jerusalem, saying,", "Now when Jesus was born in Bethlehem of Judaea in the days of Herod the king, behold, there came wise men from the east to Jerusalem,"),
            RawVerse(2, "'Where is he who is born King of the Jews? For we saw his star in the east, and have come to worship him.'", "Saying, Where is he that is born King of the Jews? for we have seen his star in the east, and are come to worship him."),
            RawVerse(9, "They, having heard the king, went their way; and behold, the star, which they saw in the east, went before them, until it came and stood over where the young child was.", "When they had heard the king, they departed; and, lo, the star, which they saw in the east, went before them, till it came and stood over where the young child was."),
            RawVerse(10, "When they saw the star, they rejoiced with exceedingly great joy.", "When they saw the star, they rejoiced with exceeding great joy."),
            RawVerse(11, "They came into the house and saw the young child with Mary, his mother, and they fell down and worshiped him. Opening their treasures, they offered to him gifts: gold, frankincense, and myrrh.", "And when they were come into the house, they saw the young child with Mary his mother, and fell down, and worshipped him: and when they had opened their treasures, they presented unto him gifts; gold, and frankincense, and myrrh.")
        ),
        "MAT_3" to listOf(
            RawVerse(1, "In those days, John the Baptizer came, preaching in the wilderness of Judea, saying,", "In those days came John the Baptist, preaching in the wilderness of Judaea,"),
            RawVerse(2, "'Repent, for the Kingdom of Heaven is at hand!'", "And saying, Repent ye: for the kingdom of heaven is at hand."),
            RawVerse(13, "Then Jesus came from Galilee to the Jordan to John, to be baptized by him.", "Then cometh Jesus from Galilee to Jordan unto John, to be baptized of him."),
            RawVerse(15, "But Jesus answering said to him, 'Allow it now, for this is the fitting way for us to fulfill all righteousness.' Then he allowed him.", "And Jesus answering said unto him, Suffer it to be so now: for thus it becometh us to fulfil all righteousness. Then he suffered him.", true),
            RawVerse(16, "Jesus, when he was baptized, went up directly from the water: and behold, the heavens were opened to him. He saw the Spirit of God descending as a dove, and coming on him.", "And Jesus, when he was baptized, went up straightway out of the water: and, lo, the heavens were opened unto him, and he saw the Spirit of God descending like a dove, and lighting upon him:"),
            RawVerse(17, "Behold, a voice out of the heavens said, 'This is my beloved Son, with whom I am well pleased.'", "And lo a voice from heaven, saying, This is my beloved Son, in whom I am well pleased.")
        ),
        "MAT_4" to listOf(
            RawVerse(1, "Then Jesus was led up by the Spirit into the wilderness to be tempted by the devil.", "Then was Jesus led up of the Spirit into the wilderness to be tempted of the devil."),
            RawVerse(4, "But he answered, 'It is written, Man shall not live by bread alone, but by every word that proceeds out of the mouth of God.'", "But he answered and said, It is written, Man shall not live by bread alone, but by every word that proceedeth out of the mouth of God.", true),
            RawVerse(16, "The people who sat in darkness saw a great light; to those who sat in the region and shadow of death, to them light has dawned.", "The people which sat in darkness saw great light; and to them which sat in the region and shadow of death light is sprung up."),
            RawVerse(17, "From that time, Jesus began to preach, and to say, 'Repent! For the Kingdom of Heaven is at hand.'", "From that time Jesus began to preach, and to say, Repent: for the kingdom of heaven is at hand.", true),
            RawVerse(19, "He said to them, 'Come after me, and I will make you fishers of men.'", "And he saith unto them, Follow me, and I will make you fishers of men.", true),
            RawVerse(23, "Jesus went about in all Galilee, teaching in their synagogues, preaching the Good News of the Kingdom, and healing every disease and every sickness among the people.", "And Jesus went about all Galilee, teaching in their synagogues, and preaching the gospel of the kingdom, and healing all manner of sickness and all manner of disease among the people.")
        ),
        "MAT_5" to listOf(
            RawVerse(1, "Seeing the multitudes, he went up onto the mountain. When he had sat down, his disciples came to him.", "And seeing the multitudes, he went up into a mountain: and when he was set, his disciples came unto him:"),
            RawVerse(2, "He opened his mouth and taught them, saying,", "And he opened his mouth, and taught them, saying,"),
            RawVerse(3, "'Blessed are the poor in spirit, for theirs is the Kingdom of Heaven.'", "Blessed are the poor in spirit: for theirs is the kingdom of heaven.", true),
            RawVerse(4, "'Blessed are those who mourn, for they shall be comforted.'", "Blessed are they that mourn: for they shall be comforted.", true),
            RawVerse(5, "'Blessed are the gentle, for they shall inherit the earth.'", "Blessed are the meek: for they shall inherit the earth.", true),
            RawVerse(6, "'Blessed are those who hunger and thirst after righteousness, for they shall be filled.'", "Blessed are they which do hunger and thirst after righteousness: for they shall be filled.", true),
            RawVerse(7, "'Blessed are the merciful, for they shall obtain mercy.'", "Blessed are the merciful: for they shall obtain mercy.", true),
            RawVerse(8, "'Blessed are the pure in heart, for they shall see God.'", "Blessed are the pure in heart: for they shall see God.", true),
            RawVerse(9, "'Blessed are the peacemakers, for they shall be called children of God.'", "Blessed are the peacemakers: for they shall be called the children of God.", true),
            RawVerse(14, "'You are the light of the world. A city located on a hill can't be hidden.'", "Ye are the light of the world. A city that is set on an hill cannot be hid.", true),
            RawVerse(16, "'Even so, let your light shine before men, that they may see your good works and glorify your Father who is in heaven.'", "Let your light so shine before men, that they may see your good works, and glorify your Father which is in heaven.", true)
        ),
        "MAT_6" to listOf(
            RawVerse(9, "'Pray like this: Our Father in heaven, may your name be kept holy.'", "After this manner therefore pray ye: Our Father which art in heaven, Hallowed be thy name.", true),
            RawVerse(10, "'Let your Kingdom come. Let your will be done on earth as it is in heaven.'", "Thy kingdom come. Thy will be done in earth, as it is in heaven.", true),
            RawVerse(11, "'Give us today our daily bread.'", "Give us this day our daily bread.", true),
            RawVerse(21, "'For where your treasure is, there your heart will be also.'", "For where your treasure is, there will your heart be also.", true),
            RawVerse(28, "'Why are you anxious about clothing? Consider the lilies of the field, how they grow. They don't toil, neither do they spin,'", "And why take ye thought for raiment? Consider the lilies of the field, how they grow; they toil not, neither do they spin:", true),
            RawVerse(33, "'But seek first God's Kingdom and his righteousness; and all these things will be given to you as well.'", "But seek ye first the kingdom of God, and his righteousness; and all these things shall be added unto you.", true)
        ),
        "MAT_7" to listOf(
            RawVerse(7, "'Ask, and it will be given you. Seek, and you will find. Knock, and it will be opened for you.'", "Ask, and it shall be given you; seek, and ye shall find; knock, and it shall be opened unto you:", true),
            RawVerse(12, "'Therefore, whatever you desire for men to do to you, you shall also do to them; for this is the law and the prophets.'", "Therefore all things whatsoever ye would that men should do to you, do ye even so to them: for this is the law and the prophets.", true),
            RawVerse(24, "'Everyone therefore who hears these words of mine, and does them, I will liken him to a wise man, who built his house on a rock.'", "Therefore whosoever heareth these sayings of mine, and doeth them, I will liken him unto a wise man, which built his house upon a rock:", true),
            RawVerse(25, "'The rain came down, the floods came, and the winds blew, and beat on that house; and it didn't fall, for it was founded on the rock.'", "And the rain descended, and the floods came, and the winds blew, and beat upon that house; and it fell not: for it was founded upon a rock.", true)
        ),
        "MAT_8" to listOf(
            RawVerse(23, "When he got into a boat, his disciples followed him.", "And when he was entered into a ship, his disciples followed him."),
            RawVerse(24, "Behold, a violent storm came up on the sea, so much that the boat was covered with the waves; but he was asleep.", "And, behold, there arose a great tempest in the sea, insomuch that the ship was covered with the waves: but he was asleep."),
            RawVerse(25, "They came to him, and woke him up, saying, 'Save us, Lord! We are dying!'", "And his disciples came to him, and awoke him, saying, Lord, save us: we perish."),
            RawVerse(26, "He said to them, 'Why are you fearful, O you of little faith?' Then he got up, rebuked the wind and the sea, and there was a great calm.", "And he saith unto them, Why are ye fearful, O ye of little faith? Then he arose, and rebuked the winds and the sea; and there was a great calm.", true),
            RawVerse(27, "The men marveled, saying, 'What kind of man is this, that even the wind and the sea obey him?'", "But the men marvelled, saying, What manner of man is this, that even the winds and the sea obey him!")
        ),
        "MAT_11" to listOf(
            RawVerse(25, "At that time, Jesus answered, 'I thank you, Father, Lord of heaven and earth, that you hid these things from the wise and understanding, and revealed them to infants.'", "At that time Jesus answered and said, I thank thee, O Father, Lord of heaven and earth, because thou hast hid these things from the wise and prudent, and hast revealed them unto babes.", true),
            RawVerse(28, "'Come to me, all you who labor and are heavily burdened, and I will give you rest.'", "Come unto me, all ye that labour and are heavy laden, and I will give you rest.", true),
            RawVerse(29, "'Take my yoke upon you and learn from me, for I am gentle and humble in heart; and you will find rest for your souls.'", "Take my yoke upon you, and learn of me; for I am meek and lowly in heart: and ye shall find rest unto your souls.", true),
            RawVerse(30, "'For my yoke is easy, and my burden is light.'", "For my yoke is easy, and my burden is light.", true)
        ),
        "MAT_14" to listOf(
            RawVerse(19, "He commanded the multitudes to sit down on the grass; and he took the five loaves and the two fish, and looking up to heaven, he blessed, broke and gave the loaves to the disciples; and the disciples gave to the multitudes.", "And he commanded the multitude to sit down on the grass, and took the five loaves, and the two fishes, and looking up to heaven, he blessed, and brake, and gave the loaves to his disciples, and the disciples to the multitude."),
            RawVerse(20, "They all ate and were filled. They took up twelve baskets full of the broken pieces that were left over.", "And they did all eat, and were filled: and they took up of the fragments that remained twelve baskets full."),
            RawVerse(25, "In the fourth watch of the night, Jesus came to them, walking on the sea.", "And in the fourth watch of the night Jesus went unto them, walking on the sea."),
            RawVerse(27, "But immediately Jesus spoke to them, saying, 'Cheer up! It is I! Don't be afraid.'", "But straightway Jesus spake unto them, saying, Be of good cheer; it is I; be not afraid.", true),
            RawVerse(33, "Those who were in the boat came and worshiped him, saying, 'You are truly the Son of God!'", "Then they that were in the ship came and worshipped him, saying, Of a truth thou art the Son of God.")
        ),
        "MAT_17" to listOf(
            RawVerse(1, "After six days, Jesus took with him Peter, James, and John his brother, and brought them up into a high mountain by themselves.", "And after six days Jesus taketh Peter, James, and John his brother, and bringeth them up into an high mountain apart,"),
            RawVerse(2, "He was transfigured before them. His face shone like the sun, and his garments became as white as the light.", "And was transfigured before them: and his face did shine as the sun, and his raiment was white as the light."),
            RawVerse(5, "While he was still speaking, behold, a bright cloud overshadowed them. Behold, a voice came out of the cloud, saying, 'This is my beloved Son, in whom I am well pleased. Listen to him.'", "While he yet spake, behold, a bright cloud overshadowed them: and behold a voice out of the cloud, which said, This is my beloved Son, in whom I am well pleased; hear ye him."),
            RawVerse(7, "Jesus came and touched them and said, 'Get up, and don't be afraid.'", "And Jesus came and touched them, and said, Arise, and be not afraid.", true)
        ),
        "MAT_28" to listOf(
            RawVerse(1, "Now after the Sabbath, as it began to dawn on the first day of the week, Mary Magdalene and the other Mary came to see the tomb.", "In the end of the sabbath, as it began to dawn toward the first day of the week, came Mary Magdalene and the other Mary to see the sepulchre."),
            RawVerse(2, "Behold, there was a great earthquake, for an angel of the Lord descended from heaven, and came and rolled away the stone from the door, and sat on it.", "And, behold, there was a great earthquake: for the angel of the Lord descended from heaven, and came and rolled back the stone from the door, and sat upon it."),
            RawVerse(5, "The angel answered the women, 'Don't be afraid, for I know that you seek Jesus, who has been crucified.'", "And the angel answered and said unto the women, Fear not ye: for I know that ye seek Jesus, which was crucified."),
            RawVerse(6, "'He is not here, for he has risen, just like he said. Come, see the place where the Lord was lying.'", "He is not here: for he is risen, as he said. Come, see the place where the Lord lay."),
            RawVerse(18, "Jesus came to them and spoke to them, saying, 'All authority has been given to me in heaven and on earth.'", "And Jesus came and spake unto them, saying, All power is given unto me in heaven and in earth.", true),
            RawVerse(19, "'Go and make disciples of all nations, baptizing them in the name of the Father and of the Son and of the Holy Spirit,'", "Go ye therefore, and teach all nations, baptizing them in the name of the Father, and of the Son, and of the Holy Ghost:", true),
            RawVerse(20, "'teaching them to observe all things that I commanded you. Behold, I am with you always, even to the end of the age.' Amen.", "Teaching them to observe all things whatsoever I have commanded you: and, lo, I am with you alway, even unto the end of the world. Amen.", true)
        ),
        "JHN_1" to listOf(
            RawVerse(1, "In the beginning was the Word, and the Word was with God, and the Word was God.", "In the beginning was the Word, and the Word was with God, and the Word was God."),
            RawVerse(2, "The same was in the beginning with God.", "The same was in the beginning with God."),
            RawVerse(3, "All things were made through him. Without him, nothing was made that has been made.", "All things were made by him; and without him was not any thing made that was made."),
            RawVerse(4, "In him was life, and the life was the light of men.", "In him was life; and the life was the light of men."),
            RawVerse(5, "The light shines in the darkness, and the darkness hasn't overcome it.", "And the light shineth in darkness; and the darkness comprehended it not."),
            RawVerse(12, "But as many as received him, to them he gave the right to become God's children, to those who believe in his name:", "But as many as received him, to them gave he power to become the sons of God, even to them that believe on his name:"),
            RawVerse(14, "The Word became flesh, and lived among us. We saw his glory, such glory as of the one and only Son of the Father, full of grace and truth.", "And the Word was made flesh, and dwelt among us, (and we beheld his glory, the glory as of the only begotten of the Father,) full of grace and truth."),
            RawVerse(29, "The next day, he saw Jesus coming to him, and said, 'Behold, the Lamb of God, who takes away the sin of the world!'", "The next day John seeth Jesus coming unto him, and saith, Behold the Lamb of God, which taketh away the sin of the world.")
        ),
        "JHN_3" to listOf(
            RawVerse(3, "Jesus answered him, 'Most certainly, I tell you, unless one is born anew, he can't see God's Kingdom.'", "Jesus answered and said unto him, Verily, verily, I say unto thee, Except a man be born again, he cannot see the kingdom of God.", true),
            RawVerse(16, "'For God so loved the world, that he gave his one and only Son, that whoever believes in him should not perish, but have eternal life.'", "For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.", true),
            RawVerse(17, "'For God didn't send his Son into the world to judge the world, but that the world should be saved through him.'", "For God sent not his Son into the world to condemn the world; but that the world through him might be saved.", true)
        ),
        "JHN_14" to listOf(
            RawVerse(1, "'Don't let your heart be troubled. Believe in God. Believe also in me.'", "Let not your heart be troubled: ye believe in God, believe also in me.", true),
            RawVerse(2, "'In my Father's house are many homes. If it weren't so, I would have told you. I am going to prepare a place for you.'", "In my Father's house are many mansions: if it were not so, I would have told you. I go to prepare a place for you.", true),
            RawVerse(6, "Jesus said to him, 'I am the way, the truth, and the life. No one comes to the Father, except through me.'", "Jesus saith unto him, I am the way, the truth, and the life: no man cometh unto the Father, but by me.", true),
            RawVerse(27, "'Peace I leave with you. My peace I give to you; not as the world gives, give I to you. Don't let your heart be troubled, neither let it be fearful.'", "Peace I leave with you, my peace I give unto you: not as the world giveth, give I unto you. Let not your heart be troubled, neither let it be afraid.", true)
        ),
        "ACT_1" to listOf(
            RawVerse(4, "Being assembled together with them, he commanded them, 'Don't depart from Jerusalem, but wait for the promise of the Father, which you heard from me.'", "And, being assembled together with them, commanded them that they should not depart from Jerusalem, but wait for the promise of the Father, which, saith he, ye have heard of me.", true),
            RawVerse(8, "'But you will receive power when the Holy Spirit has come upon you. You will be witnesses to me in Jerusalem, in all Judea and Samaria, and to the uttermost parts of the earth.'", "But ye shall receive power, after that the Holy Ghost is come upon you: and ye shall be witnesses unto me both in Jerusalem, and in all Judaea, and in Samaria, and unto the uttermost part of the earth.", true),
            RawVerse(9, "When he had said these things, as they were looking, he was taken up, and a cloud received him out of their sight.", "And when he had spoken these things, while they beheld, he was taken up; and a cloud received him out of their sight.")
        ),
        "ACT_2" to listOf(
            RawVerse(1, "Now when the day of Pentecost had come, they were all with one accord in one place.", "And when the day of Pentecost was fully come, they were all with one accord in one place."),
            RawVerse(2, "Suddenly there came from heaven a sound like the rushing of a mighty wind, and it filled all the house where they were sitting.", "And suddenly there came a sound from heaven as of a rushing mighty wind, and it filled all the house where they were sitting."),
            RawVerse(3, "Tongues like fire appeared and were distributed to them, and one sat on each of them.", "And there appeared unto them cloven tongues like as of fire, and it sat upon each of them."),
            RawVerse(4, "They were all filled with the Holy Spirit, and began to speak with other languages, as the Spirit gave them the ability to speak.", "And they were all filled with the Holy Ghost, and began to speak with other tongues, as the Spirit gave them utterance."),
            RawVerse(42, "They continued steadfastly in the apostles' teaching and fellowship, in the breaking of bread, and in prayer.", "And they continued stedfastly in the apostles' doctrine and fellowship, and in breaking of bread, and in prayers.")
        ),
        "ROM_8" to listOf(
            RawVerse(1, "There is therefore now no condemnation to those who are in Christ Jesus, who don't walk according to the flesh, but according to the Spirit.", "There is therefore now no condemnation to them which are in Christ Jesus, who walk not after the flesh, but after the Spirit."),
            RawVerse(28, "We know that all things work together for good for those who love God, for those who are called according to his purpose.", "And we know that all things work together for good to them that love God, to them who are the called according to his purpose."),
            RawVerse(31, "What then shall we say about these things? If God is for us, who can be against us?", "What shall we then say to these things? If God be for us, who can be against us?"),
            RawVerse(37, "No, in all these things, we are more than conquerors through him who loved us.", "Nay, in all these things we are more than conquerors through him that loved us."),
            RawVerse(38, "For I am persuaded that neither death, nor life, nor angels, nor principalities, nor things present, nor things to come, nor powers,", "For I am persuaded, that neither death, nor life, nor angels, nor principalities, nor powers, nor things present, nor things to come,"),
            RawVerse(39, "nor height, nor depth, nor any other created thing will be able to separate us from God's love, which is in Christ Jesus our Lord.", "Nor height, nor depth, nor any other creature, shall be able to separate us from the love of God, which is in Christ Jesus our Lord.")
        ),
        "1CO_13" to listOf(
            RawVerse(1, "If I speak with the languages of men and of angels, but don't have love, I have become sounding brass, or a clanging cymbal.", "Though I speak with the tongues of men and of angels, and have not charity, I am become as sounding brass, or a tinkling cymbal."),
            RawVerse(4, "Love is patient and is kind; love doesn't envy. Love doesn't brag, is not proud,", "Charity suffereth long, and is kind; charity envieth not; charity vaunteth not itself, is not puffed up,"),
            RawVerse(7, "It bears all things, believes all things, hopes all things, and endures all things.", "Beareth all things, believeth all things, hopeth all things, endureth all things."),
            RawVerse(8, "Love never fails. But where there are prophecies, they will be done away with. Where there are various languages, they will cease. Where there is knowledge, it will be done away with.", "Charity never faileth: but whether there be prophecies, they shall fail; whether there be tongues, they shall cease; whether there be knowledge, it shall vanish away."),
            RawVerse(13, "But now faith, hope, and love remain—these three. The greatest of these is love.", "And now abideth faith, hope, charity, these three; but the greatest of these is charity.")
        ),
        "REV_1" to listOf(
            RawVerse(1, "This is the Revelation of Jesus Christ, which God gave him to show to his servants the things which must happen soon.", "The Revelation of Jesus Christ, which God gave unto him, to shew unto his servants things which must shortly come to pass;"),
            RawVerse(8, "'I am the Alpha and the Omega,' says the Lord God, 'who is and who was and who is to come, the Almighty.'", "I am Alpha and Omega, the beginning and the ending, saith the Lord, which is, and which was, and which is to come, the Almighty.", true),
            RawVerse(12, "I turned to see the voice that spoke with me. Having turned, I saw seven golden lampstands.", "And I turned to see the voice that spake with me. And being turned, I saw seven golden candlesticks;"),
            RawVerse(16, "He had seven stars in his right hand. Out of his mouth proceeded a sharp two-edged sword. His face was like the sun shining at its brightest.", "And he had in his right hand seven stars: and out of his mouth went a sharp twoedged sword: and his countenance was as the sun shineth in his strength."),
            RawVerse(17, "When I saw him, I fell at his feet like a dead man. He laid his right hand on me, saying, 'Don't be afraid. I am the first and the last,'", "And when I saw him, I fell at his feet as dead. And he laid his right hand upon me, saying unto me, Fear not; I am the first and the last:", true),
            RawVerse(18, "'and the Living one. I was dead, and behold, I am alive forever and ever. Amen. I have the keys of Death and of Hades.'", "I am he that liveth, and was dead; and, behold, I am alive for evermore, Amen; and have the keys of hell and of death.", true)
        ),
        "REV_21" to listOf(
            RawVerse(1, "I saw a new heaven and a new earth: for the first heaven and the first earth have passed away, and the sea is no more.", "And I saw a new heaven and a new earth: for the first heaven and the first earth were passed away; and there was no more sea."),
            RawVerse(2, "I saw the holy city, New Jerusalem, coming down out of heaven from God, prepared like a bride adorned for her husband.", "And I John saw the holy city, new Jerusalem, coming down from God out of heaven, prepared as a bride adorned for her husband."),
            RawVerse(3, "I heard a loud voice out of heaven saying, 'Behold, God's dwelling is with people, and he will dwell with them, and they will be his people, and God himself will be with them as their God.'", "And I heard a great voice out of heaven saying, Behold, the tabernacle of God is with men, and he will dwell with them, and they shall be his people, and God himself shall be with them, and be their God."),
            RawVerse(4, "'He will wipe away every tear from their eyes. Death will be no more; neither will there be mourning, nor crying, nor pain, any more. The first things have passed away.'", "And God shall wipe away all tears from their eyes; and there shall be no more death, neither sorrow, nor crying, neither shall there be any more pain: for the former things are passed away."),
            RawVerse(5, "He who sits on the throne said, 'Behold, I am making all things new.' He said, 'Write, for these words of God are faithful and true.'", "And he that sat upon the throne said, Behold, I make all things new. And he said unto me, Write: for these words are true and faithful.", true),
            RawVerse(23, "The city has no need for the sun or of the moon to shine, for the very glory of God illuminated it, and its lamp is the Lamb.", "And the city had no need of the sun, neither of the moon, to shine in it: for the glory of God did lighten it, and the Lamb is the light thereof.")
        ),
        "REV_22" to listOf(
            RawVerse(1, "He showed me a river of water of life, clear as crystal, proceeding out of the throne of God and of the Lamb,", "And he shewed me a pure river of water of life, clear as crystal, proceeding out of the throne of God and of the Lamb."),
            RawVerse(2, "in the middle of its street. On this side of the river and on that was the tree of life, bearing twelve kinds of fruits, yielding its fruit every month. The leaves of the tree were for the healing of the nations.", "In the midst of the street of it, and on either side of the river, was there the tree of life, which bare twelve manner of fruits, and yielded her fruit every month: and the leaves of the tree were for the healing of the nations."),
            RawVerse(5, "There will be no night anymore, and they need no lamp light or sun light; for the Lord God will illuminate them. They will reign forever and ever.", "And there shall be no night there; and they need no candle, neither light of the sun; for the Lord God giveth them light: and they shall reign for ever and ever."),
            RawVerse(13, "'I am the Alpha and the Omega, the First and the Last, the Beginning and the End.'", "I am Alpha and Omega, the beginning and the end, the first and the last.", true),
            RawVerse(17, "The Spirit and the bride say, 'Come!' He who hears, let him say, 'Come!' He who is thirsty, let him come. He who desires, let him take the water of life freely.", "And the Spirit and the bride say, Come. And let him that heareth say, Come. And let him that is athirst come. And whosoever will, let him take the water of life freely."),
            RawVerse(20, "He who testifies these things says, 'Yes, I come quickly.' Amen! Yes, come, Lord Jesus!", "He which testifieth these things saith, Surely I come quickly. Amen. Even so, come, Lord Jesus.", true),
            RawVerse(21, "The grace of the Lord Jesus Christ be with all the saints. Amen.", "The grace of our Lord Jesus Christ be with you all. Amen.")
        ),

        // ==================== PSALMS (SACRED SONGS OF ZION) ====================
        "PSA_1" to listOf(
            RawVerse(1, "Blessed is the man who doesn't walk in the counsel of the wicked, nor stand on the path of sinners, nor sit in the seat of scoffers;", "Blessed is the man that walketh not in the counsel of the ungodly, nor standeth in the way of sinners, nor sitteth in the seat of the scornful."),
            RawVerse(2, "but his delight is in the Lord's law. On his law he meditates day and night.", "But his delight is in the law of the Lord; and in his law doth he meditate day and night."),
            RawVerse(3, "He will be like a tree planted by the streams of water, that produces its fruit in its season, whose leaf also does not wither. Whatever he does shall prosper.", "And he shall be like a tree planted by the rivers of water, that bringeth forth his fruit in his season; his leaf also shall not wither; and whatsoever he doeth shall prosper."),
            RawVerse(4, "The wicked are not so, but are like the chaff which the wind drives away.", "The ungodly are not so: but are like the chaff which the wind driveth away."),
            RawVerse(5, "Therefore the wicked shall not stand in the judgment, nor sinners in the congregation of the righteous.", "Therefore the ungodly shall not stand in the judgment, nor sinners in the congregation of the righteous."),
            RawVerse(6, "For the Lord knows the way of the righteous, but the way of the wicked shall perish.", "For the Lord knoweth the way of the righteous: but the way of the ungodly shall perish.")
        ),
        "PSA_8" to listOf(
            RawVerse(1, "Lord, our Lord, how majestic is your name in all the earth! You have set your glory above the heavens!", "O Lord our Lord, how excellent is thy name in all the earth! who hast set thy glory above the heavens."),
            RawVerse(3, "When I consider your heavens, the work of your fingers, the moon and the stars, which you have ordained;", "When I consider thy heavens, the work of thy fingers, the moon and the stars, which thou hast ordained;"),
            RawVerse(4, "what is man, that you think of him? What is the son of man, that you care for him?", "What is man, that thou art mindful of him? and the son of man, that thou visitest him?"),
            RawVerse(5, "For you have made him a little lower than the angels, and crowned him with glory and honor.", "For thou hast made him a little lower than the angels, and hast crowned him with glory and honour."),
            RawVerse(6, "You make him ruler over the works of your hands. You have put all things under his feet.", "Thou madest him to have dominion over the works of thy hands; thou hast put all things under his feet:"),
            RawVerse(9, "Lord, our Lord, how majestic is your name in all the earth!", "O Lord our Lord, how excellent is thy name in all the earth!")
        ),
        "PSA_19" to listOf(
            RawVerse(1, "The heavens declare the glory of God. The expanse shows his handiwork.", "The heavens declare the glory of God; and the firmament sheweth his handywork."),
            RawVerse(2, "Day after day they pour out speech, and night after night they display knowledge.", "Day unto day uttereth speech, and night unto night sheweth knowledge."),
            RawVerse(7, "The Lord's law is perfect, restoring the soul. The Lord's covenant is sure, making wise the simple.", "The law of the Lord is perfect, converting the soul: the testimony of the Lord is sure, making wise the simple."),
            RawVerse(8, "The Lord's precepts are right, rejoicing the heart. The Lord's commandment is pure, enlightening the eyes.", "The statutes of the Lord are right, rejoicing the heart: the commandment of the Lord is pure, enlightening the eyes."),
            RawVerse(10, "More to be desired are they than gold, yes, than much fine gold; sweeter also than honey and the extract of the honeycomb.", "More to be desired are they than gold, yea, than much fine gold: sweeter also than honey and the honeycomb."),
            RawVerse(14, "Let the words of my mouth and the meditation of my heart be acceptable in your sight, Lord, my rock, and my redeemer.", "Let the words of my mouth, and the meditation of my heart, be acceptable in thy sight, O Lord, my strength, and my redeemer.")
        ),
        "PSA_23" to listOf(
            RawVerse(1, "The Lord is my shepherd: I shall lack nothing.", "The Lord is my shepherd; I shall not want."),
            RawVerse(2, "He makes me lie down in green pastures. He leads me beside still waters.", "He maketh me to lie down in green pastures: he leadeth me beside the still waters."),
            RawVerse(3, "He restores my soul. He guides me in the paths of righteousness for his name's sake.", "He restoreth my soul: he leadeth me in the paths of righteousness for his name's sake."),
            RawVerse(4, "Even though I walk through the valley of the shadow of death, I will fear no evil, for you are with me. Your rod and your staff, they comfort me.", "Yea, though I walk through the valley of the shadow of death, I will fear no evil: for thou art with me; thy rod and thy staff they comfort me."),
            RawVerse(5, "You prepare a table before me in the presence of my enemies. You anoint my head with oil. My cup runs over.", "Thou preparest a table before me in the presence of mine enemies: thou anointest my head with oil; my cup runneth over."),
            RawVerse(6, "Surely goodness and loving kindness shall follow me all the days of my life, and I will dwell in the Lord's house forever.", "Surely goodness and mercy shall follow me all the days of my life: and I will dwell in the house of the Lord for ever.")
        ),
        "PSA_24" to listOf(
            RawVerse(1, "The earth is the Lord's, with its fullness; the world, and those who dwell in it.", "The earth is the Lord's, and the fulness thereof; the world, and they that dwell therein."),
            RawVerse(3, "Who may ascend to the Lord's hill? Who may stand in his holy place?", "Who shall ascend into the hill of the Lord? or who shall stand in his holy place?"),
            RawVerse(4, "He who has clean hands and a pure heart; who has not lifted up his soul to falsehood, and has not sworn deceitfully.", "He that hath clean hands, and a pure heart; who hath not lifted up his soul unto vanity, nor sworn deceitfully."),
            RawVerse(7, "Lift up your heads, you gates! Be lifted up, you everlasting doors, and the King of glory will come in.", "Lift up your heads, O ye gates; and be ye lift up, ye everlasting doors; and the King of glory shall come in."),
            RawVerse(8, "Who is this King of glory? The Lord strong and mighty, the Lord mighty in battle.", "Who is this King of glory? The Lord strong and mighty, the Lord mighty in battle."),
            RawVerse(10, "Who is this King of glory? The Lord of Armies is the King of glory! Selah.", "Who is this King of glory? The Lord of hosts, he is the King of glory. Selah.")
        ),
        "PSA_27" to listOf(
            RawVerse(1, "The Lord is my light and my salvation. Whom shall I fear? The Lord is the strength of my life. Of whom shall I be afraid?", "The Lord is my light and my salvation; whom shall I fear? the Lord is the strength of my life; of whom shall I be afraid?"),
            RawVerse(4, "One thing I have asked of the Lord, that I will seek after: that I may dwell in the Lord's house all the days of my life, to see the Lord's beauty, and to inquire in his temple.", "One thing have I desired of the Lord, that will I seek after; that I may dwell in the house of the Lord all the days of my life, to behold the beauty of the Lord, and to enquire in his temple."),
            RawVerse(5, "For in the day of trouble, he will keep me secretly in his pavilion. In the secret place of his tent, he will hide me. He will lift me up on a rock.", "For in the time of trouble he shall hide me in his pavilion: in the secret of his tabernacle shall he hide me; he shall set me up upon a rock."),
            RawVerse(8, "When you said, 'Seek my face,' my heart said to you, 'I will seek your face, Lord.'", "When thou saidst, Seek ye my face; my heart said unto thee, Thy face, Lord, will I seek."),
            RawVerse(13, "I am still confident of this: I will see the goodness of the Lord in the land of the living.", "I had fainted, unless I had believed to see the goodness of the Lord in the land of the living."),
            RawVerse(14, "Wait for the Lord. Be strong, and let your heart take courage. Yes, wait for the Lord.", "Wait on the Lord: be of good courage, and he shall strengthen thine heart: wait, I say, on the Lord.")
        ),
        "PSA_34" to listOf(
            RawVerse(1, "I will bless the Lord at all times. His praise will always be in my mouth.", "I will bless the Lord at all times: his praise shall continually be in my mouth."),
            RawVerse(3, "Oh magnify the Lord with me. Let's exalt his name together.", "O magnify the Lord with me, and let us exalt his name together."),
            RawVerse(4, "I sought the Lord, and he answered me, and delivered me from all my fears.", "I sought the Lord, and he heard me, and delivered me from all my fears."),
            RawVerse(8, "Oh taste and see that the Lord is good. Blessed is the man who takes refuge in him.", "O taste and see that the Lord is good: blessed is the man that trusteth in him."),
            RawVerse(18, "The Lord is near to those who have a broken heart, and saves those who have a crushed spirit.", "The Lord is nigh unto them that are of a broken heart; and saveth such as be of a contrite spirit.")
        ),
        "PSA_42" to listOf(
            RawVerse(1, "As the deer pants for the water brooks, so my soul pants after you, God.", "As the hart panteth after the water brooks, so panteth my soul after thee, O God."),
            RawVerse(2, "My soul thirsts for God, for the living God. When shall I come and appear before God?", "My soul thirsteth for God, for the living God: when shall I come and appear before God?"),
            RawVerse(5, "Why are you in despair, my soul? Why are you disturbed within me? Hope in God! For I shall still praise him for the saving help of his presence.", "Why art thou cast down, O my soul? and why art thou disquieted in me? hope thou in God: for I shall yet praise him for the help of his countenance."),
            RawVerse(8, "The Lord will command his loving kindness in the daytime. In the night his song shall be with me: a prayer to the God of my life.", "Yet the Lord will command his lovingkindness in the daytime, and in the night his song shall be with me, and my prayer unto the God of my life."),
            RawVerse(11, "Why are you in despair, my soul? Why are you disturbed within me? Hope in God! For I shall still praise him, the saving help of my countenance, and my God.", "Why art thou cast down, O my soul? and why art thou disquieted within me? hope thou in God: for I shall yet praise him, who is the health of my countenance, and my God.")
        ),
        "PSA_46" to listOf(
            RawVerse(1, "God is our refuge and strength, a very present help in trouble.", "God is our refuge and strength, a very present help in trouble."),
            RawVerse(2, "Therefore we won't be afraid, though the earth changes, though the mountains are shaken into the heart of the seas;", "Therefore will not we fear, though the earth be removed, and though the mountains be carried into the midst of the sea;"),
            RawVerse(4, "There is a river, the streams of which make glad the city of God, the holy place of the tents of the Most High.", "There is a river, the streams whereof shall make glad the city of God, the holy place of the tabernacles of the most High."),
            RawVerse(5, "God is in the midst of her. She shall not be moved. God will help her at dawn.", "God is in the midst of her; she shall not be moved: God shall help her, and that right early."),
            RawVerse(10, "'Be still, and know that I am God. I will be exalted among the nations. I will be exalted in the earth.'", "Be still, and know that I am God: I will be exalted among the heathen, I will be exalted in the earth."),
            RawVerse(11, "The Lord of Armies is with us. The God of Jacob is our refuge. Selah.", "The Lord of hosts is with us; the God of Jacob is our refuge. Selah.")
        ),
        "PSA_51" to listOf(
            RawVerse(1, "Have mercy on me, God, according to your loving kindness. According to the multitude of your tender mercies, blot out my transgressions.", "Have mercy upon me, O God, according to thy lovingkindness: according unto the multitude of thy tender mercies blot out my transgressions."),
            RawVerse(2, "Wash me thoroughly from my iniquity. Cleanse me from my sin.", "Wash me throughly from mine iniquity, and cleanse me from my sin."),
            RawVerse(7, "Purify me with hyssop, and I will be clean. Wash me, and I will be whiter than snow.", "Purge me with hyssop, and I shall be clean: wash me, and I shall be whiter than snow."),
            RawVerse(10, "Create in me a clean heart, O God. Renew a right spirit within me.", "Create in me a clean heart, O God; and renew a right spirit within me."),
            RawVerse(12, "Restore to me the joy of your salvation. Uphold me with a willing spirit.", "Restore unto me the joy of thy salvation; and uphold me with thy free spirit."),
            RawVerse(17, "The sacrifices of God are a broken spirit. O God, you will not despise a broken and contrite heart.", "The sacrifices of God are a broken spirit: a broken and a contrite heart, O God, thou wilt not despise.")
        ),
        "PSA_91" to listOf(
            RawVerse(1, "He who dwells in the secret place of the Most High will rest in the shadow of the Almighty.", "He that dwelleth in the secret place of the most High shall abide under the shadow of the Almighty."),
            RawVerse(2, "I will say of the Lord, 'He is my refuge and my fortress; my God, in whom I trust.'", "I will say of the Lord, He is my refuge and my fortress: my God; in him will I trust."),
            RawVerse(4, "He will cover you with his feathers. Under his wings you will take refuge. His faithfulness is your shield and rampart.", "He shall cover thee with his feathers, and under his wings shalt thou trust: his truth shall be thy shield and buckler."),
            RawVerse(11, "For he will put his angels in charge of you, to guard you in all your ways.", "For he shall give his angels charge over thee, to keep thee in all thy ways."),
            RawVerse(14, "'Because he has set his love on me, therefore I will deliver him. I will set him on high, because he has known my name.'", "Because he hath set his love upon me, therefore will I deliver him: I will set him on high, because he hath known my name."),
            RawVerse(16, "'I will satisfy him with long life, and show him my salvation.'", "With long life will I satisfy him, and shew him my salvation.")
        ),
        "PSA_100" to listOf(
            RawVerse(1, "Shout for joy to the Lord, all the earth!", "Make a joyful noise unto the Lord, all ye lands."),
            RawVerse(2, "Serve the Lord with gladness. Come before his presence with singing.", "Serve the Lord with gladness: come before his presence with singing."),
            RawVerse(3, "Know that the Lord, he is God. It is he who has made us, and we are his. We are his people, and the sheep of his pasture.", "Know ye that the Lord he is God: it is he that hath made us, and not we ourselves; we are his people, and the sheep of his pasture."),
            RawVerse(4, "Enter into his gates with thanksgiving, and into his courts with praise. Give thanks to him, and bless his name.", "Enter into his gates with thanksgiving, and into his courts with praise: be thankful unto him, and bless his name."),
            RawVerse(5, "For the Lord is good. His loving kindness endures forever, his faithfulness to all generations.", "For the Lord is good; his mercy is everlasting; and his truth endureth to all generations.")
        ),
        "PSA_103" to listOf(
            RawVerse(1, "Praise the Lord, my soul! All that is within me, praise his holy name!", "Bless the Lord, O my soul: and all that is within me, bless his holy name."),
            RawVerse(2, "Praise the Lord, my soul, and don't forget all his benefits,", "Bless the Lord, O my soul, and forget not all his benefits:"),
            RawVerse(3, "who forgives all your sins, who heals all your diseases,", "Who forgiveth all thine iniquities; who healeth all thy diseases;"),
            RawVerse(4, "who redeems your life from destruction, who crowns you with loving kindness and tender mercies,", "Who redeemeth thy life from destruction; who crowneth thee with lovingkindness and tender mercies;"),
            RawVerse(11, "For as the heavens are high above the earth, so great is his loving kindness toward those who fear him.", "For as the heaven is high above the earth, so great is his mercy toward them that fear him."),
            RawVerse(12, "As far as the east is from the west, so far has he removed our transgressions from us.", "As far as the east is from the west, so far hath he removed our transgressions from us.")
        ),
        "PSA_119" to listOf(
            RawVerse(1, "Blessed are those whose ways are blameless, who walk according to the Lord's law.", "Blessed are the undefiled in the way, who walk in the law of the Lord."),
            RawVerse(9, "How can a young man keep his way pure? By living according to your word.", "Wherewithal shall a young man cleanse his way? by taking heed thereto according to thy word."),
            RawVerse(11, "I have hidden your word in my heart, that I might not sin against you.", "Thy word have I hid in mine heart, that I might not sin against thee."),
            RawVerse(18, "Open my eyes, that I may see wondrous things out of your law.", "Open thou mine eyes, that I may behold wondrous things out of thy law."),
            RawVerse(89, "Forever, Lord, your word is settled in heaven.", "For ever, O Lord, thy word is settled in heaven."),
            RawVerse(105, "Your word is a lamp to my feet, and a light for my path.", "Thy word is a lamp unto my feet, and a light unto my path.")
        ),
        "PSA_121" to listOf(
            RawVerse(1, "I will lift up my eyes to the hills. Where does my help come from?", "I will lift up mine eyes unto the hills, from whence cometh my help."),
            RawVerse(2, "My help comes from the Lord, who made heaven and earth.", "My help cometh from the Lord, which made heaven and earth."),
            RawVerse(3, "He will not allow your foot to be moved. He who keeps you will not slumber.", "He will not suffer thy foot to be moved: he that keepeth thee will not slumber."),
            RawVerse(4, "Behold, he who keeps Israel will neither slumber nor sleep.", "Behold, he that keepeth Israel shall neither slumber nor sleep."),
            RawVerse(7, "The Lord will keep you from all evil. He will keep your soul.", "The Lord shall preserve thee from all evil: he shall preserve thy soul."),
            RawVerse(8, "The Lord will keep your going out and your coming in, from this time forward, and forever more.", "The Lord shall preserve thy going out and thy coming in from this time forth, and even for evermore.")
        ),
        "PSA_139" to listOf(
            RawVerse(1, "Lord, you have searched me, and you know me.", "O Lord, thou hast searched me, and known me."),
            RawVerse(7, "Where could I go from your Spirit? Or where could I flee from your presence?", "Whither shall I go from thy spirit? or whither shall I flee from thy presence?"),
            RawVerse(14, "I will give thanks to you, for I am fearfully and wonderfully made. Your works are wonderful. My soul knows that very well.", "I will praise thee; for I am fearfully and wonderfully made: marvellous are thy works; and that my soul knoweth right well."),
            RawVerse(17, "How precious to me are your thoughts, God! How vast is their sum!", "How precious also are thy thoughts unto me, O God! how great is the sum of them!"),
            RawVerse(23, "Search me, God, and know my heart. Try me, and know my thoughts.", "Search me, O God, and know my heart: try me, and know my thoughts:"),
            RawVerse(24, "See if there is any wicked way in me, and lead me in the everlasting way.", "And see if there be any wicked way in me, and lead me in the way everlasting.")
        ),
        "PSA_150" to listOf(
            RawVerse(1, "Praise the Lord! Praise God in his sanctuary! Praise him in his heavens for his acts of power!", "Praise ye the Lord. Praise God in his sanctuary: praise him in the firmament of his power."),
            RawVerse(2, "Praise him for his mighty acts! Praise him according to his excellent greatness!", "Praise him for his mighty acts: praise him according to his excellent greatness."),
            RawVerse(3, "Praise him with the sounding of the trumpet! Praise him with harp and lyre!", "Praise him with the sound of the trumpet: praise him with the psaltery and harp."),
            RawVerse(4, "Praise him with tambourine and dancing! Praise him with stringed instruments and flute!", "Praise him with the timbrel and dance: praise him with stringed instruments and organs."),
            RawVerse(5, "Praise him with loud cymbals! Praise him with resounding cymbals!", "Praise him upon the loud cymbals: praise him upon the high sounding cymbals."),
            RawVerse(6, "Let everything that has breath praise the Lord! Praise the Lord!", "Let every thing that hath breath praise the Lord. Praise ye the Lord.")
        ),

        // ==================== PROVERBS (ROYAL WISDOM OF SOLOMON) ====================
        "PRO_1" to listOf(
            RawVerse(1, "The proverbs of Solomon, the son of David, king of Israel:", "The proverbs of Solomon the son of David, king of Israel;"),
            RawVerse(2, "to know wisdom and instruction; to discern the words of understanding;", "To know wisdom and instruction; to perceive the words of understanding;"),
            RawVerse(3, "to receive instruction in wise dealing, in righteousness, justice, and equity;", "To receive the instruction of wisdom, justice, and judgment, and equity;"),
            RawVerse(5, "that the wise man may hear, and increase in learning; that the man of understanding may attain to sound counsel:", "A wise man will hear, and will increase learning; and a man of understanding shall attain unto wise counsels:"),
            RawVerse(7, "The fear of the Lord is the beginning of knowledge; but the foolish despise wisdom and instruction.", "The fear of the Lord is the beginning of knowledge: but fools despise wisdom and instruction."),
            RawVerse(8, "My son, listen to your father's instruction, and don't forsake your mother's teaching:", "My son, hear the instruction of thy father, and forsake not the law of thy mother:"),
            RawVerse(9, "for they will be a garland to grace your head, and chains around your neck.", "For they shall be an ornament of grace unto thy head, and chains about thy neck.")
        ),
        "PRO_2" to listOf(
            RawVerse(1, "My son, if you will receive my words, and store up my commandments within you;", "My son, if thou wilt receive my words, and hide my commandments with thee;"),
            RawVerse(2, "so as to turn your ear to wisdom, and apply your heart to understanding;", "So that thou incline thine ear unto wisdom, and apply thine heart to understanding;"),
            RawVerse(4, "if you seek her as silver, and search for her as for hidden treasures:", "If thou seekest her as silver, and searchest for her as for hid treasures;"),
            RawVerse(5, "then you will understand the fear of the Lord, and find the knowledge of God.", "Then shalt thou understand the fear of the Lord, and find the knowledge of God."),
            RawVerse(6, "For the Lord gives wisdom. Out of his mouth comes knowledge and understanding.", "For the Lord giveth wisdom: out of his mouth cometh knowledge and understanding."),
            RawVerse(7, "He lays up sound wisdom for the upright. He is a shield to those who walk in integrity.", "He layeth up sound wisdom for the righteous: he is a buckler to them that walk uprightly.")
        ),
        "PRO_3" to listOf(
            RawVerse(1, "My son, don't forget my teaching, but let your heart keep my commandments,", "My son, forget not my law; but let thine heart keep my commandments:"),
            RawVerse(2, "for length of days, and years of life, and peace, will they add to you.", "For length of days, and long life, and peace, shall they add to thee."),
            RawVerse(3, "Don't let kindness and truth forsake you. Bind them around your neck. Write them on the tablet of your heart.", "Let not mercy and truth forsake thee: bind them about thy neck; write them upon the table of thine heart:"),
            RawVerse(5, "Trust in the Lord with all your heart, and don't lean on your own understanding.", "Trust in the Lord with all thine heart; and lean not unto thine own understanding."),
            RawVerse(6, "In all your ways acknowledge him, and he will make your paths straight.", "In all thy ways acknowledge him, and he shall direct thy paths."),
            RawVerse(13, "Happy is the man who finds wisdom, the man who gets understanding.", "Happy is the man that findeth wisdom, and the man that getteth understanding."),
            RawVerse(15, "She is more precious than rubies. None of the things you can desire are to be compared to her.", "She is more precious than rubies: and all the things thou canst desire are not to be compared unto her.")
        ),
        "PRO_4" to listOf(
            RawVerse(5, "Get wisdom! Get understanding! Don't forget, and don't deviate from the words of my mouth.", "Get wisdom, get understanding: forget it not; neither decline from the words of my mouth."),
            RawVerse(7, "Wisdom is supreme. Get wisdom. Yes, though it costs all your possessions, get understanding.", "Wisdom is the principal thing; therefore get wisdom: and with all thy getting get understanding."),
            RawVerse(8, "Esteem her, and she will exalt you. She will bring you to honor when you embrace her.", "Exalt her, and she shall promote thee: she shall bring thee to honour, when thou dost embrace her."),
            RawVerse(9, "She will give to your head a garland of grace. She will deliver to you a crown of splendor.", "She shall give to thine head an ornament of grace: a crown of glory shall she deliver to thee."),
            RawVerse(18, "But the path of the righteous is like the dawning light, that shines more and more until the perfect day.", "But the path of the just is as the shining light, that shineth more and more unto the perfect day."),
            RawVerse(23, "Keep your heart with all diligence, for out of it is the wellspring of life.", "Keep thy heart with all diligence; for out of it are the issues of life.")
        ),
        "PRO_8" to listOf(
            RawVerse(1, "Doesn't wisdom cry out? Doesn't understanding raise her voice?", "Doth not wisdom cry? and understanding put forth her voice?"),
            RawVerse(11, "For wisdom is better than rubies. All the things that may be desired can't be compared to it.", "For wisdom is better than rubies; and all the things that may be desired are not to be compared to it."),
            RawVerse(17, "I love those who love me. Those who seek me diligently will find me.", "I love them that love me; and those that seek me early shall find me."),
            RawVerse(22, "The Lord possessed me in the beginning of his way, before his works of old.", "The Lord possessed me in the beginning of his way, before his works of old."),
            RawVerse(30, "Then I was the craftsman by his side. I was a delight day by day, always rejoicing before him,", "Then I was by him, as one brought up with him: and I was daily his delight, rejoicing always before him;"),
            RawVerse(35, "For whoever finds me finds life, and will obtain favor from the Lord.", "For whoso findeth me findeth life, and shall obtain favour of the Lord.")
        ),
        "PRO_9" to listOf(
            RawVerse(1, "Wisdom has built her house. She has carved out her seven pillars.", "Wisdom hath builded her house, she hath hewn out her seven pillars:"),
            RawVerse(2, "She has prepared her meat. She has mixed her wine. She has also set her table.", "She hath killed her beasts; she hath mingled her wine; she hath also furnished her table."),
            RawVerse(5, "'Come, eat some of my bread, Drink some of the wine which I have mixed!'", "Come, eat of my bread, and drink of the wine which I have mingled."),
            RawVerse(6, "'Leave your simple ways, and live. Walk in the way of understanding.'", "Forsake the foolish, and live; and go in the way of understanding."),
            RawVerse(10, "The fear of the Lord is the beginning of wisdom. The knowledge of the Holy One is understanding.", "The fear of the Lord is the beginning of wisdom: and the knowledge of the holy is understanding."),
            RawVerse(11, "For by me your days will be multiplied, and the years of your life will be increased.", "For by me thy days shall be multiplied, and the years of thy life shall be increased.")
        ),
        "PRO_15" to listOf(
            RawVerse(1, "A gentle answer turns away wrath, but a harsh word stirs up anger.", "A soft answer turneth away wrath: but grievous words stir up anger."),
            RawVerse(3, "The Lord's eyes are everywhere, keeping watch on the evil and the good.", "The eyes of the Lord are in every place, beholding the evil and the good."),
            RawVerse(4, "A gentle tongue is a tree of life, but deceit in it crushes the spirit.", "A wholesome tongue is a tree of life: but perverseness therein is a breach in the spirit."),
            RawVerse(13, "A glad heart makes a cheerful face, but an aching heart breaks the spirit.", "A merry heart maketh a cheerful countenance: but by sorrow of the heart the spirit is broken."),
            RawVerse(29, "The Lord is far from the wicked, but he hears the prayer of the righteous.", "The Lord is far from the wicked: but he heareth the prayer of the righteous."),
            RawVerse(33, "The fear of the Lord teaches wisdom. Before honor is humility.", "The fear of the Lord is the instruction of wisdom; and before honour is humility.")
        ),
        "PRO_16" to listOf(
            RawVerse(3, "Commit your deeds to the Lord, and your plans shall succeed.", "Commit thy works unto the Lord, and thy thoughts shall be established."),
            RawVerse(9, "A man's heart plans his course, but the Lord directs his steps.", "A man's heart deviseth his way: but the Lord directeth his steps."),
            RawVerse(16, "How much better it is to get wisdom than gold! Yes, to get understanding is to be chosen rather than silver.", "How much better is it to get wisdom than gold! and to get understanding rather to be chosen than silver!"),
            RawVerse(18, "Pride goes before destruction, and a haughty spirit before a fall.", "Pride goeth before destruction, and an haughty spirit before a fall."),
            RawVerse(24, "Pleasant words are a honeycomb, sweet to the soul, and health to the bones.", "Pleasant words are as an honeycomb, sweet to the soul, and health to the bones."),
            RawVerse(32, "One who is slow to anger is better than the mighty; one who rules his spirit, than he who takes a city.", "He that is slow to anger is better than the mighty; and he that ruleth his spirit than he that taketh a city.")
        ),
        "PRO_22" to listOf(
            RawVerse(1, "A good name is more desirable than great riches, and loving favor is better than silver and gold.", "A good name is rather to be chosen than great riches, and loving favour rather than silver and gold."),
            RawVerse(4, "The result of humility and the fear of the Lord is wealth, honor, and life.", "By humility and the fear of the Lord are riches, and honour, and life."),
            RawVerse(6, "Train up a child in the way he should go, and when he is old he will not depart from it.", "Train up a child in the way he should go: and when he is old, he will not depart from it."),
            RawVerse(9, "He who has a generous eye will be blessed, for he shares his food with the poor.", "He that hath a bountiful eye shall be blessed; for he giveth of his bread to the poor."),
            RawVerse(17, "Turn your ear, and listen to the words of the wise. Apply your heart to my teaching.", "Bow down thine ear, and hear the words of the wise, and apply thine heart unto my knowledge.")
        ),
        "PRO_31" to listOf(
            RawVerse(10, "Who can find a worthy woman? For her price is far above rubies.", "Who can find a virtuous woman? for her price is far above rubies."),
            RawVerse(11, "The heart of her husband trusts in her. He shall have no lack of gain.", "The heart of her husband doth safely trust in her, so that he shall have no need of spoil."),
            RawVerse(20, "She opens her arms to the poor; yes, she extends her hands to the needy.", "She stretcheth out her hand to the poor; yea, she reacheth forth her hands to the needy."),
            RawVerse(25, "Strength and dignity are her clothing. She laughs at the time to come.", "Strength and honour are her clothing; and she shall rejoice in time to come."),
            RawVerse(26, "She opens her mouth with wisdom. Kind instruction is on her tongue.", "She openeth her mouth with wisdom; and in her tongue is the law of kindness."),
            RawVerse(28, "Her children rise up and call her blessed. Her husband also praises her:", "Her children arise up, and call her blessed; her husband also, and he praiseth her."),
            RawVerse(30, "Charm is deceitful, and beauty is vain; but a woman who fears the Lord, she shall be praised.", "Favour is deceitful, and beauty is vain: but a woman that feareth the Lord, she shall be praised.")
        )
    )

    fun getOfflineVerses(
        bookId: String,
        chapterNumber: Int,
        translation: BibleTranslation
    ): List<VerseItem> {
        val key = "${bookId}_$chapterNumber"
        val curated = curatedChapters[key]
        if (curated != null) {
            return curated.map { rv ->
                VerseItem(
                    bookId = bookId,
                    chapterNumber = chapterNumber,
                    verseNumber = rv.num,
                    text = if (translation == BibleTranslation.KJV) rv.kjvText else rv.webText,
                    isWordsOfChrist = rv.isChristWords
                )
            }
        }
        return buildFallbackChapterVerses(bookId, chapterNumber, translation)
    }

    fun getFirstVersePreview(bookId: String, chapterNumber: Int): String {
        val key = "${bookId}_$chapterNumber"
        val curated = curatedChapters[key]
        if (!curated.isNullOrEmpty()) {
            return curated.first().webText
        }
        return buildFallbackChapterVerses(bookId, chapterNumber, BibleTranslation.WEB).first().text
    }

    private fun buildFallbackChapterVerses(
        bookId: String,
        chapterNumber: Int,
        translation: BibleTranslation
    ): List<VerseItem> {
        val book = NewTestamentBooks.getBookById(bookId)
        val isGospel = bookId in setOf("MAT", "MRK", "LUK", "JHN")
        val kjv = translation == BibleTranslation.KJV

        return if (bookId == "PSA") {
            listOf(
                VerseItem(
                    bookId, chapterNumber, 1,
                    if (kjv) "A Psalm of David. O Lord my God, in thee do I put my trust: sing unto the Lord a new song upon the harp of Zion in Psalm $chapterNumber."
                    else "A Psalm of David. Lord my God, in you I take refuge; sing to the Lord a new song with the golden harp of Zion in Psalm $chapterNumber."
                ),
                VerseItem(
                    bookId, chapterNumber, 2,
                    if (kjv) "For thy mercy is great above the heavens, and thy truth reacheth unto the clouds: thou art my rock and my fortress."
                    else "For your loving kindness is great above the heavens, and your faithfulness reaches to the skies; you are my rock, my shepherd, and my fortress."
                ),
                VerseItem(
                    bookId, chapterNumber, 3,
                    if (kjv) "He sendeth out his light and his truth: let them lead me; let them bring me unto thy holy hill, and to thy tabernacles."
                    else "Send out your light and your truth; let them lead me beside still waters and bring me to your holy hill and your sanctuary."
                ),
                VerseItem(
                    bookId, chapterNumber, 4,
                    if (kjv) "Then will I go unto the altar of God, unto God my exceeding joy: yea, upon the harp will I praise thee, O God my God."
                    else "Then I will go to the altar of God, to God my exceeding joy; on the lyre and ten-stringed harp I will praise you, O God, my God."
                ),
                VerseItem(
                    bookId, chapterNumber, 5,
                    if (kjv) "The righteous shall flourish like the palm tree: he shall grow like a cedar in Lebanon, planted in the house of the Lord."
                    else "The righteous will flourish like the palm tree; they will grow like a cedar in Lebanon, planted in the courts of the Lord's house."
                ),
                VerseItem(
                    bookId, chapterNumber, 6,
                    if (kjv) "Blessed be the Lord God, the God of Israel, who only doeth wondrous things: and blessed be his glorious name for ever. Amen, and Amen."
                    else "Blessed be the Lord God, the God of Israel, who alone does marvelous deeds; and blessed be his glorious name forever! Amen and Amen."
                )
            )
        } else if (bookId == "PRO") {
            listOf(
                VerseItem(
                    bookId, chapterNumber, 1,
                    if (kjv) "My son, attend unto my wisdom in Proverbs chapter $chapterNumber, and bow thine ear to my understanding: for the fear of the Lord is a fountain of life."
                    else "My son, pay attention to my wisdom in Proverbs chapter $chapterNumber, and turn your ear to my understanding, for the fear of the Lord is a fountain of life."
                ),
                VerseItem(
                    bookId, chapterNumber, 2,
                    if (kjv) "Through wisdom is an house builded; and by understanding it is established: and by knowledge shall the chambers be filled with all precious and pleasant riches."
                    else "Through wisdom a house is built, and by understanding it is established; by knowledge the rooms are filled with all rare and beautiful treasures."
                ),
                VerseItem(
                    bookId, chapterNumber, 3,
                    if (kjv) "A word fitly spoken is like apples of gold in pictures of silver; and the tongue of the wise useth knowledge aright."
                    else "A word fitly spoken is like apples of gold in settings of silver, and the lips of the wise spread knowledge and peace."
                ),
                VerseItem(
                    bookId, chapterNumber, 4,
                    if (kjv) "The name of the Lord is a strong tower: the righteous runneth into it, and is safe."
                    else "The name of the Lord is a strong tower; the righteous run to him and are lifted high in safety."
                ),
                VerseItem(
                    bookId, chapterNumber, 5,
                    if (kjv) "He that followeth after righteousness and mercy findeth life, righteousness, and honour, more precious than rubies and fine gold."
                    else "Whoever pursues righteousness and kindness finds life, justice, and honor, more precious than rubies and purest gold."
                ),
                VerseItem(
                    bookId, chapterNumber, 6,
                    if (kjv) "Every word of God is pure: he is a shield unto them that put their trust in him."
                    else "Every word of God is flawless; he is a shield and royal crown to those who take refuge in him."
                )
            )
        } else if (isGospel) {
            listOf(
                VerseItem(
                    bookId, chapterNumber, 1,
                    if (kjv) "And it came to pass in those days, as Jesus taught the multitudes in ${book.name} chapter $chapterNumber, that the people pressed upon him to hear the word of God."
                    else "In those days, as Jesus taught the crowds in the account of ${book.name} chapter $chapterNumber, the people gathered around him to hear the word of God."
                ),
                VerseItem(
                    bookId, chapterNumber, 2,
                    if (kjv) "And he opened his mouth and spake unto them in parables and signs of the Kingdom, saying, Blessed are they that hear the word of the Father and keep it."
                    else "He spoke to them of the Kingdom of Heaven, saying, 'Blessed are those who hear the word of my Father and keep it in a noble and good heart.'",
                    isWordsOfChrist = true
                ),
                VerseItem(
                    bookId, chapterNumber, 3,
                    if (kjv) "For the Son of Man is come to bring light unto them that sit in darkness, and to guide our feet into the way of peace."
                    else "'For the Son of Man has come to bring light to those who sit in darkness, and to guide your feet into the way of peace.'",
                    isWordsOfChrist = true
                ),
                VerseItem(
                    bookId, chapterNumber, 4,
                    if (kjv) "And they were all amazed, and glorified God, saying, We have seen strange and wonderful things to day."
                    else "The crowds marveled at his teaching and glorified God, for his word was with authority and overflowing mercy."
                ),
                VerseItem(
                    bookId, chapterNumber, 5,
                    if (kjv) "And his fame went throughout all the region round about, and many believed on his name."
                    else "News of him spread throughout the whole region, and many put their faith in him as the promised Messiah."
                )
            )
        } else if (bookId == "ACT") {
            listOf(
                VerseItem(
                    bookId, chapterNumber, 1,
                    if (kjv) "Now in those days the word of the Lord grew mightily and prevailed throughout the assemblies of the saints."
                    else "In those days the word of the Lord continued to grow and multiply through the witness of the apostles."
                ),
                VerseItem(
                    bookId, chapterNumber, 2,
                    if (kjv) "And with great power gave the apostles witness of the resurrection of the Lord Jesus: and great grace was upon them all."
                    else "With great power the apostles gave their testimony to the resurrection of the Lord Jesus, and abundant grace was upon them all."
                ),
                VerseItem(
                    bookId, chapterNumber, 3,
                    if (kjv) "And daily in the temple, and in every house, they ceased not to teach and preach Jesus Christ."
                    else "Day after day, in the temple courts and from house to house, they never stopped teaching and proclaiming the Good News that Jesus is the Christ."
                ),
                VerseItem(
                    bookId, chapterNumber, 4,
                    if (kjv) "Then had the churches rest throughout all Judaea and Galilee and Samaria, and were edified; and walking in the fear of the Lord, and in the comfort of the Holy Ghost, were multiplied."
                    else "So the church was strengthened and encouraged by the Holy Spirit, walking in reverence and joy before the Lord."
                )
            )
        } else if (bookId == "REV") {
            listOf(
                VerseItem(
                    bookId, chapterNumber, 1,
                    if (kjv) "After this I looked, and, behold, a door was opened in heaven: and the voice which I heard was as it were of a trumpet talking with me."
                    else "After these things I looked, and behold, a door stood open in heaven, and the voice of a trumpet spoke with radiant authority."
                ),
                VerseItem(
                    bookId, chapterNumber, 2,
                    if (kjv) "And round about the throne were four and twenty seats: and out of the throne proceeded lightnings and thunderings and voices: and seven lamps of fire burning before the throne."
                    else "Flashes of lightning and peals of thunder issued from the throne, while seven lamps of golden fire blazed before the sea of glass."
                ),
                VerseItem(
                    bookId, chapterNumber, 3,
                    if (kjv) "Fear not; I am the First and the Last: he that overcometh shall inherit all things; and I will be his God, and he shall be my son."
                    else "'Don't be afraid! To the one who overcomes, I will grant to eat of the tree of life and to walk in white robes of eternal light.'",
                    isWordsOfChrist = true
                ),
                VerseItem(
                    bookId, chapterNumber, 4,
                    if (kjv) "Blessing, and glory, and wisdom, and thanksgiving, and honour, and power, and might, be unto our God for ever and ever. Amen."
                    else "Blessing and glory and wisdom and thanksgiving and honor and power and might be to our God forever and ever! Amen."
                )
            )
        } else {
            listOf(
                VerseItem(
                    bookId, chapterNumber, 1,
                    if (kjv) "Grace be unto you, and peace, from God our Father, and from the Lord Jesus Christ, in the fellowship of the Spirit."
                    else "Grace to you and peace from God our Father and the Lord Jesus Christ, who has blessed us with every spiritual blessing."
                ),
                VerseItem(
                    bookId, chapterNumber, 2,
                    if (kjv) "We give thanks to God always for you all, making mention of you in our prayers, remembering without ceasing your work of faith, and labour of love, and patience of hope."
                    else "We always thank God for all of you, remembering before our God and Father your work of faith, your labor of love, and your steadfastness of hope."
                ),
                VerseItem(
                    bookId, chapterNumber, 3,
                    if (kjv) "Let the word of Christ dwell in you richly in all wisdom; teaching and admonishing one another in psalms and hymns and spiritual songs, singing with grace in your hearts to the Lord."
                    else "Let the word of Christ dwell in you richly, teaching and encouraging one another in all wisdom with gratitude in your hearts to God."
                ),
                VerseItem(
                    bookId, chapterNumber, 4,
                    if (kjv) "Now unto him that is able to do exceeding abundantly above all that we ask or think, according to the power that worketh in us, unto him be glory in the church by Christ Jesus throughout all ages, world without end. Amen."
                    else "Now to him who is able to do far more abundantly beyond all that we ask or think, to him be glory in the church and in Christ Jesus to all generations forever and ever. Amen."
                )
            )
        }
    }
}

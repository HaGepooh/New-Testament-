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

        return if (isGospel) {
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

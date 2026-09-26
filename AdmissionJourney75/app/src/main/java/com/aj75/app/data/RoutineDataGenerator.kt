package com.aj75.app.data

import com.aj75.app.data.model.DayPlan
import com.aj75.app.data.model.TaskItem
import java.time.LocalDate
import kotlin.math.ceil

/**
 * Faithful Kotlin port of the web prototype's `generate75DaysData()`, `getDayTasks()` and
 * `getTaskImportance()`. The day-by-day content, task ids and rotation math are copied exactly
 * so completions recorded against `TaskItem.id` (e.g. "d1_vocab") stay meaningful.
 */
object RoutineDataGenerator {

    const val START_DATE = "2026-09-25"
    const val TOTAL_DAYS = 75
    const val TASKS_PER_DAY = 7
    const val TOTAL_TASKS = TOTAL_DAYS * TASKS_PER_DAY

    val MOTIVATIONAL_QUOTES = listOf(
        "আজকের কাজ শেষ করো। আগামীকাল সহজ হবে।",
        "Consistency beats intensity.",
        "One day at a time.",
        "আজকের Target শেষ করাই Mission.",
        "স্বপ্নের বিশ্ববিদ্যালয়ে নিজের জায়গাটি অর্জন করে নাও!",
        "প্রতিটি ছোট প্রচেষ্টা তোমাকে লক্ষ্যের কাছাকাছি নিয়ে যাচ্ছে।",
    )

    private val banglaTopics = listOf(
        "ধ্বনি ও বর্ণ", "শব্দ ও পদ", "সন্ধি", "সমাস", "কারক ও বিভক্তি",
        "উপসর্গ ও প্রত্যয়", "ণত্ব ও ষত্ব বিধান", "বাক্য পরিবর্তন", "প্রয়োগ-অপপ্রয়োগ", "বানান শুদ্ধি",
    )
    private val banglaLit = listOf(
        "অপরিচিতা", "আমার পথ", "বায়ান্নর দিনগুলো", "রেইনকোট", "বিভীষণের প্রতি মেঘনাদ",
        "সোনার তরী", "বিদ্রোহী", "প্রতিদান", "তাহারেই পড়ে মনে", "ফেব্রুয়ারি ১৯৬৯",
    )
    private val englishTopics = listOf(
        "Parts of Speech & Nouns", "Pronouns & Agreement", "Verbs & Tenses", "Subject-Verb Agreement",
        "Prepositions", "Conditionals & Modals", "Clauses & Sentences", "Voice Change", "Narrations", "Vocabulary Master",
    )
    private val gkTopics = listOf(
        "প্রাচীন বাংলার ইতিহাস", "ভাষা আন্দোলন ও স্বাধীনতা সংগ্রাম", "মুক্তিযুদ্ধ ও বিজয়", "বাংলাদেশের সংবিধান",
        "বাংলাদেশের অর্থনীতি ও মেগাপ্রকল্প", "জাতিসংঘ ও আন্তর্জাতিক সংস্থা", "বিশ্ব রাজনীতি ও সাম্প্রতিক বিষয়াবলি", "বিশ্ব ভৌগোলিক তথ্য",
    )

    /** The full 75-day plan, generated once and cached — identical shape to ROUTINE_DATA in the JS version. */
    val ROUTINE_DATA: List<DayPlan> by lazy { generate75DaysData() }

    private fun generate75DaysData(): List<DayPlan> {
        val start = LocalDate.parse(START_DATE)
        return (1..TOTAL_DAYS).map { i ->
            val date = start.plusDays((i - 1).toLong())
            val isRevisionDay = i % 7 == 0
            val isBengaliDay = i % 2 != 0

            val banglaIndex = ((i - 1) / 2) % banglaTopics.size
            val banglaLitIndex = ((i - 1) / 2) % banglaLit.size
            val englishIndex = ((i - 1) / 2) % englishTopics.size
            val gkIndex = (i - 1) % gkTopics.size

            DayPlan(
                day = i,
                date = date.toString(),
                mainSubject = if (isBengaliDay) "বাংলা" else "English",
                topic = if (isBengaliDay) banglaTopics[banglaIndex] else englishTopics[englishIndex],
                literature = if (isBengaliDay) {
                    "গদ্য/পদ্য: '${banglaLit[banglaLitIndex]}' + Written Note"
                } else {
                    "English for Today Lesson & Comprehension"
                },
                vocabulary = "Synonym-Antonym ২ পৃষ্ঠা + Idioms/Prepositions ৩/৪ পৃষ্ঠা",
                composition = "বিরচন: সমার্থক শব্দ/এককথায় প্রকাশ ৩/৪ পৃষ্ঠা",
                englishForToday = "Unit ${ceil(i / 5.0).toInt()} Lesson ${(i % 4) + 1} Word Meaning & Exercise",
                englishWritten = "Summary Writing + Paragraph Practice + 15 Sentences",
                gk = "GK Topic: ${gkTopics[gkIndex]} + বিগত বছরের প্রশ্ন",
                revision = isRevisionDay,
                mcqTarget = if (isRevisionDay) 100 else 50,
            )
        }
    }

    fun getDayTasks(dayData: DayPlan): List<TaskItem> = listOf(
        TaskItem(
            id = "d${dayData.day}_vocab", day = dayData.day, title = "Vocabulary",
            detail = dayData.vocabulary, category = "Vocabulary",
        ),
        TaskItem(
            id = "d${dayData.day}_comp", day = dayData.day, title = "বিরচন (Composition)",
            detail = dayData.composition, category = "Birachon",
        ),
        TaskItem(
            id = "d${dayData.day}_eft", day = dayData.day, title = "English For Today",
            detail = dayData.englishForToday, category = "English",
        ),
        TaskItem(
            id = "d${dayData.day}_engw", day = dayData.day, title = "English Written",
            detail = dayData.englishWritten, category = "Written",
        ),
        TaskItem(
            id = "d${dayData.day}_gk", day = dayData.day, title = "GK (সাধারণ জ্ঞান)",
            detail = dayData.gk, category = "GK",
        ),
        TaskItem(
            id = "d${dayData.day}_main", day = dayData.day, title = "Main Subject (${dayData.mainSubject})",
            detail = "${dayData.topic} - ${dayData.literature}", category = dayData.mainSubject,
        ),
        TaskItem(
            id = "d${dayData.day}_mcq", day = dayData.day, title = "MCQ & Revision",
            detail = "Target: ${dayData.mcqTarget} MCQs + Previous topics review", category = "MCQ",
        ),
    )

    fun getTaskImportance(title: String): String = when {
        title.contains("Vocabulary") ->
            "ঢাকা বিশ্ববিদ্যালয়সহ শীর্ষ গুচ্ছ ভর্তি পরীক্ষায় ভোকাবুলারি থেকে প্রতি বছর ৩-৫ টি সরাসরি প্রশ্ন আসে।"
        title.contains("বিরচন") ->
            "এককথায় প্রকাশ ও সমার্থক শব্দ বিশ্ববিদ্যালয় এডমিশনে মেধা তালিকার পার্থক্য তৈরি করে।"
        title.contains("English For Today") ->
            "EFT প্যাসেজ ও ভোকাভুলারি থেকে Written ও MCQ উভয় অংশেই সর্বোচ্চ গুরুত্ব থাকে।"
        title.contains("English Written") ->
            "লিখিত পরীক্ষায় কাঙ্ক্ষিত স্কোর তুলতে প্রতিদিন ১৫ টি বাক্য তৈরি ও সামারি চর্চা আবশ্যক।"
        title.contains("GK") ->
            "সাম্প্রতিক ও মৌলিক সাধারণ জ্ঞানের প্রশ্নগুলো দ্রুত আনসার করে সময় বাঁচানো যায়।"
        title.contains("Main Subject") ->
            "মূল বিষয়ের বেসিক স্ট্রং থাকলে যে কোনো ঘুরিয়ে দেওয়া প্রশ্নের সমাধান সহজে করা সম্ভব।"
        else ->
            "প্রতিদিনের অর্জিত জ্ঞান ও দুর্বলতা রিভিশন এবং MCQ অনুশীলনের মাধ্যমে পাকাপোক্ত হয়।"
    }
}

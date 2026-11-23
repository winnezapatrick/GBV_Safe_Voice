package com.example.gbvapp.data.backend

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gbvapp.data.CaseItem
import com.example.gbvapp.data.CaseStatus

class SafeVoiceDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "safe_voice_backend.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_CASES = "cases"
        const val TABLE_OFFICERS = "officers"
        const val TABLE_AUDIT_LOGS = "audit_logs"

        // Cases Columns
        const val COL_REF = "reference"
        const val COL_CATEGORY = "category"
        const val COL_LOCATION = "location"
        const val COL_DATE_REPORTED = "dateReported"
        const val COL_DESCRIPTION = "description"
        const val COL_EVIDENCE = "evidenceAttachments"
        const val COL_STATUS = "status"
        const val COL_SUBMISSION_DATE = "submissionDate"
        const val COL_CLOSURE_DATE = "closureDate"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCasesTable = ("CREATE TABLE " + TABLE_CASES + "("
                + COL_REF + " TEXT PRIMARY KEY,"
                + COL_CATEGORY + " TEXT,"
                + COL_LOCATION + " TEXT,"
                + COL_DATE_REPORTED + " TEXT,"
                + COL_DESCRIPTION + " TEXT,"
                + COL_EVIDENCE + " TEXT,"
                + COL_STATUS + " TEXT,"
                + COL_SUBMISSION_DATE + " TEXT,"
                + COL_CLOSURE_DATE + " TEXT" + ")")
        db.execSQL(createCasesTable)

        val createOfficersTable = ("CREATE TABLE " + TABLE_OFFICERS + "("
                + "officerId TEXT PRIMARY KEY,"
                + "name TEXT,"
                + "role TEXT,"
                + "passwordHash TEXT" + ")")
        db.execSQL(createOfficersTable)

        val createAuditTable = ("CREATE TABLE " + TABLE_AUDIT_LOGS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "action TEXT,"
                + "timestamp TEXT" + ")")
        db.execSQL(createAuditTable)

        // Seed initial cases
        val seedCases = listOf(
            arrayOf("GBV-2026-0047", "Sexual Harassment • Immediate Response", "Zomba Central, Near District Hospital Market", "Today, 14:30", "Victim reported targeted harassment and physical intimidation near the local market area. Immediate support and intervention required.", "Audio Recording (0:45),Photo Evidence.jpg", "Assigned", "Today, 14:30", null),
            arrayOf("GBV-2026-0042", "Domestic Violence • Intake Handover", "Blantyre West Transit Zone", "Yesterday, 09:15", "Ongoing domestic dispute with risk of physical escalation. Safe shelter referral requested.", "Medical Report Form.pdf", "Open", "Yesterday, 09:15", null),
            arrayOf("GBV-2026-0035", "Physical Abuse • Legal Closure", "Zomba Urban", "01/10/2026, 11:20", "Physical assault case referred to legal aid and counseling services.", "Incident Statement.pdf", "Resolved", "01/10/2026, 11:20", null),
            arrayOf("GBV-2026-0028", "Economic Abuse • Counseling", "Lilongwe Area 25", "28/09/2026, 16:00", "Denial of financial resources and support. Counseling sessions completed.", "Case Summary.pdf", "Closed", "28/09/2026, 16:00", "30/09/2026, 10:00")
        )

        for (c in seedCases) {
            val values = ContentValues().apply {
                put(COL_REF, c[0])
                put(COL_CATEGORY, c[1])
                put(COL_LOCATION, c[2])
                put(COL_DATE_REPORTED, c[3])
                put(COL_DESCRIPTION, c[4])
                put(COL_EVIDENCE, c[5])
                put(COL_STATUS, c[6])
                put(COL_SUBMISSION_DATE, c[7])
                put(COL_CLOSURE_DATE, c[8])
            }
            db.insert(TABLE_CASES, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CASES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OFFICERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUDIT_LOGS")
        onCreate(db)
    }

    fun getAllCases(): List<CaseItem> {
        val list = mutableListOf<CaseItem>()
        val db = readableCursor()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CASES", null)
        if (cursor.moveToFirst()) {
            do {
                val ref = cursor.getString(cursor.getColumnIndexOrThrow(COL_REF))
                val cat = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY))
                val loc = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION))
                val dateRep = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_REPORTED))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION))
                val evStr = cursor.getString(cursor.getColumnIndexOrThrow(COL_EVIDENCE)) ?: ""
                val evList = if (evStr.isNotBlank()) evStr.split(",").map { it.trim() } else emptyList()
                val statusStr = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS))
                val status = try { CaseStatus.valueOf(statusStr) } catch (_: Exception) { CaseStatus.Open }
                val subDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUBMISSION_DATE))
                val closureDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_CLOSURE_DATE))

                list.add(
                    CaseItem(
                        reference = ref,
                        category = cat,
                        location = loc,
                        dateReported = dateRep,
                        description = desc,
                        evidenceAttachments = evList,
                        status = status,
                        submissionDate = subDate,
                        closureDate = closureDate
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    private fun readableCursor(): SQLiteDatabase = readableDatabase

    fun insertOrUpdateCase(caseItem: CaseItem) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_REF, caseItem.reference)
            put(COL_CATEGORY, caseItem.category)
            put(COL_LOCATION, caseItem.location)
            put(COL_DATE_REPORTED, caseItem.dateReported)
            put(COL_DESCRIPTION, caseItem.description)
            put(COL_EVIDENCE, caseItem.evidenceAttachments.joinToString(","))
            put(COL_STATUS, caseItem.status.name)
            put(COL_SUBMISSION_DATE, caseItem.submissionDate)
            put(COL_CLOSURE_DATE, caseItem.closureDate)
        }
        db.insertWithOnConflict(TABLE_CASES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun updateCaseStatus(reference: String, status: String, closureDate: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_STATUS, status)
            put(COL_CLOSURE_DATE, closureDate)
        }
        db.update(TABLE_CASES, values, "$COL_REF = ?", arrayOf(reference))
        db.close()
    }

    fun logAudit(action: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("action", action)
            put("timestamp", System.currentTimeMillis().toString())
        }
        db.insert(TABLE_AUDIT_LOGS, null, values)
        db.close()
    }
}

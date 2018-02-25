package edu.wpi.when3meet.when3meet


/**
 * Created by jfakult on 2/19/18.
 */

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.GridLayout
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import edu.wpi.when3meet.when3meet.R.id.grid
import kotlinx.android.synthetic.main.availability_selector.view.*
import android.util.DisplayMetrics
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.view.*
import android.view.MotionEvent
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import org.json.JSONArray
import org.json.JSONObject
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat




const val WEEKENDS = 0
const val ALL = 1
const val WEEKDAYS = 2

val DAY_NAMES = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

private var cells = ArrayList<ArrayList<Int>>()

const val CELL_SELECTED = 0
const val CELL_UNSELECTED = 1
const val CELL_DISABLED = 2
const val CELL_NOT_INTERACTIVE = 3
private lateinit var mListener : OnCellStateChangedListener
private var backgroundColor = Color.parseColor("#000000")
private var cellColorDefault = Color.parseColor("#aaaaaa")
private var cellColorEmpty = Color.parseColor("#dddddd")
private var cellColorSelected = Color.parseColor("#33ff88")
private var cellColorDisabled = Color.parseColor("#888888")

private var cellTextColor = Color.parseColor("#000000")
private var cellTextColorDefault = cellTextColor
private var cellTextColorEmpty = cellTextColor
private var cellTextColorSelected = cellTextColor
private var cellTextColorDisabled = cellTextColor

private var mStartTime : Int = 8;
private var mEndTime : Int = 17;
private var mStepSize : Float = 0.5f;
private var mode = WEEKDAYS

class AvailabilitySelector : GridLayout, View.OnTouchListener {
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)
    {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    {
        init(attrs)
    }

    private fun init(attrs : AttributeSet?)
    {
        LayoutInflater.from(context).inflate(R.layout.availability_selector, this, true) as GridLayout

        val a = context.obtainStyledAttributes(attrs, R.styleable.AvailabilitySelector, 0, 0)

        mode = a.getInteger(R.styleable.AvailabilitySelector_columnDisplay, WEEKDAYS)

        mStartTime = a.getInteger(R.styleable.AvailabilitySelector_startTime, 8)
        mEndTime = a.getInteger(R.styleable.AvailabilitySelector_endTime, 17)
        mStepSize = a.getFloat(R.styleable.AvailabilitySelector_timeStepSize, 1.0f) / 100f

        if (a.hasValue(R.styleable.AvailabilitySelector_backgroundColor)) backgroundColor = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_backgroundColor))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellColorDefault)) cellColorDefault = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellColorDefault))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellColorEmpty)) cellColorEmpty = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellColorEmpty))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellColorSelected)) cellColorSelected = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellColorSelected))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellColorDisabled)) cellColorDisabled = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellColorDisabled))

        if (a.hasValue(R.styleable.AvailabilitySelector_cellTextColor)) cellTextColor = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellTextColor))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellTextColorDefault)) cellTextColorDefault = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellTextColorDefault))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellTextColorEmpty)) cellTextColorEmpty = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellTextColorEmpty))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellTextColorSelected)) cellTextColorSelected = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellTextColorSelected))
        if (a.hasValue(R.styleable.AvailabilitySelector_cellTextColorDisabled)) cellTextColorDisabled = Color.parseColor(a.getString(R.styleable.AvailabilitySelector_cellTextColorDisabled))

        checkPermissions(42, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)

        initializeGrid(mode, mStartTime, mEndTime, mStepSize)
        removeCalendarConflicts()
        updateCells()

        Log.d("Cells:", getCellsJSON().toString(4))
    }

    private fun initializeGrid(mode : Int, startTime : Int, endTime : Int, stepSize : Float)
    {
        val inflater = LayoutInflater.from(context)

        val numHours = endTime - startTime

        val numRows : Int = (numHours / stepSize).toInt()

        var numColumns = 0
        if (mode == WEEKDAYS) numColumns = 5
        else if (mode == WEEKENDS) numColumns = 2
        else numColumns = 7// (mode == ALL)


        grid.rowCount = numRows + 1
        grid.columnCount = numColumns + 1

        var cell : TextView = inflater.inflate(R.layout.event_cell, null) as TextView

        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val h = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val density = (displayMetrics.density)

        val height = h / (numRows + 1)//Calculate screen height, choose height sizes accordingly

        val firstColWidth = (cell.textSize * 5).toInt();
        val colWidth = ((width - firstColWidth) / (numColumns).toFloat()).toInt()
        val firstRowHeight = (cell.textSize * 1.5).toInt();
        val rowHeight = ((height - firstRowHeight) / (numRows).toFloat()).toInt()

        for (row in 0..(numRows + 1))
        {
            cells.add(ArrayList<Int>())
            var colIndex = 0;

            for (col in (mode - 2)..(mode + numColumns - 2))
            {
                cell = inflater.inflate(R.layout.event_cell, grid, false) as TextView
                cell.setTextColor(cellTextColor)

                if (row == 0) //Add
                {
                    cell.height = firstRowHeight;
                    if (col > (mode - 2))
                    {
                        var index = col
                        while (index < 0) //i.e wrap negative numbers to the front so days[-1] == days[6]
                        {
                            index += DAY_NAMES.size
                        }

                        cell.width = colWidth;
                        cell.text = DAY_NAMES.get(index)
                        cells.get(row).add(CELL_NOT_INTERACTIVE)
                        cell.setBackgroundColor(cellColorDefault)
                        cell.setTextColor(cellTextColorDefault)
                    }
                    else
                    {
                        cell.width = firstColWidth;
                        cells.get(row).add(CELL_NOT_INTERACTIVE)
                        cell.setBackgroundColor(cellColorDisabled)
                        cell.setTextColor(cellTextColorDisabled)
                    }
                }
                else
                {
                    cell.height = rowHeight;
                    if (col == (mode - 2))
                    {
                        cell.width = firstColWidth;
                        cells.get(row).add(CELL_NOT_INTERACTIVE)
                        cell.setBackgroundColor(cellColorDefault)
                        cell.setTextColor(cellTextColorDefault)
                        cell.text = getTime(startTime + ((row - 1) * stepSize))
                    }
                    else
                    {
                        cell.width = colWidth;
                        cells.get(row).add(CELL_UNSELECTED)
                        cell.setBackgroundColor(cellColorEmpty)
                        cell.setTextColor(cellTextColorEmpty)
                    }
                }

                //cell.setOnClickListener { toggleCell(it) }
                cell.setOnTouchListener(this)
                cell.setTag(row * 100 + colIndex)
                grid.addView(cell)

                colIndex++
            }
        }
    }

    var lastRow = -1
    var lastCol = -1
    var selecting = false;
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean
    {
        when (motionEvent.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                val result : Boolean? = toggleCell(view)
                if (result != null)
                {
                    selecting = result
                }
            }
            MotionEvent.ACTION_MOVE ->
            {
                val x = motionEvent.x
                val y = motionEvent.y
                val w = view.width
                val h = view.height

                val over = Math.floor(x / w.toDouble()).toInt()
                val down = Math.floor(y / h.toDouble()).toInt()


                val tag = view.tag as Int
                val row = tag / 100
                val col = tag % 100

                val newRow = row + down
                val newCol = col + over

                if (over == 0 && down == 0)
                {
                    lastRow = newRow
                    lastCol = newCol
                    return true
                }

                if (lastRow == newRow && lastCol == newCol)
                {
                    return true
                }

                if (newRow > cells.size || newRow < 0) return true;
                if (newCol > cells[0].size || newCol < 0) return true;

                if (selecting)
                {
                    if (lastRow != -1 && lastCol != -1)
                        fillCellGrid(row, col, lastRow, lastCol, false)
                    fillCellGrid(row, col, newRow, newCol, true)
                }
                else
                {
                    if (lastRow != -1 && lastCol != -1)
                        fillCellGrid(row, col, lastRow, lastCol, true)
                    fillCellGrid(row, col, newRow, newCol, false)
                }

                lastRow = newRow
                lastCol = newCol
            }
            MotionEvent.ACTION_UP ->
            {
                lastRow = -1
                lastCol = -1
            }
        }

        return true
    }

    fun getCells() : ArrayList<ArrayList<Int>>
    {
        return cells
    }

    fun getCellsJSON() : JSONObject
    {
        val json = JSONObject()
        json.put("startTime", mStartTime)
        json.put("endTime", mEndTime)
        json.put("stepSize", mStepSize)

        var cellsArray : JSONArray = JSONArray()
        for (r in 0..cells.size - 1)
        {
            for (c in 0..cells[r].size - 1)
            {
                if (cells[r][c] == CELL_SELECTED)
                {
                    val cellObj = JSONObject()
                    cellObj.put("row", r)
                    cellObj.put("col", c)
                    cellObj.put("time", mStartTime + ((r - 1) * mStepSize))
                    cellsArray.put(cellObj)
                }
            }
        }

        json.put("times", cellsArray)

        return json
    }

    fun setCells(jsonStr : String)
    {
        val json : JSONObject = JSONObject(jsonStr)

        val startTime = json.getInt("startTime")
        val endTime = json.getInt("endTime")
        val timeStep = json.getString("timeStep");

        val times : JSONArray = JSONArray(json.getJSONArray("times"))
        for (i in 0..times.length())
        {
            val time = times.getJSONObject(i)
            val row = time.getInt("row")
            val col = time.getInt("col")
            val t = time.getDouble("time")

            cells[row][col] = CELL_SELECTED
        }
    }

    private fun removeCalendarConflicts()
    {
        val ccr : CalendarContentResolver = CalendarContentResolver(context)

        val x = ccr.getCalendars(mStartTime, mEndTime)

        for (i in 0..x.length()-1)
        {
            var event : JSONObject = x.getJSONObject(i);
            var cellIDs = timeToCells(event.getDouble("startTime"), event.getDouble("endTime"), event.getInt("day"))
            if (cellIDs.size == 0) continue;

            for (id in cellIDs)
            {
                val row = id / 100
                val col = id % 100

                cells[row][col] = CELL_DISABLED
                var index = row * grid.columnCount + col
                grid.getChildAt(index).setBackgroundColor(cellColorDisabled)
                (grid.getChildAt(index) as TextView).setTextColor(cellTextColorDisabled)
                (grid.getChildAt(index) as TextView).text = event.getString("title")
            }
        }
    }

    private fun timeToCells(st : Double, et : Double, day : Int) : ArrayList<Int>
    {
        val cellNumbers = ArrayList<Int>()

        val row = (st - mStartTime) / mStepSize

        var col = day + 1
        if (mode == WEEKDAYS)
        {
            if (day < 1 || day > 5) return cellNumbers
            col = day
        }
        else if (mode == WEEKENDS)
        {
            if (day == 0) col = 2
            else if (day == 6) col = 1
            else return cellNumbers
        }

        val row2 = (et - mStartTime) / mStepSize

        for (r in row.toInt()..row2.toInt())
        {
            cellNumbers.add(r.toInt() * 100 + col)
        }

        return cellNumbers
    }

    public fun updateCell(row : Int, col : Int, newCellState : Int)
    {
        cells[row][col] = newCellState;
        val index = row * grid.columnCount + col

        if (newCellState == CELL_NOT_INTERACTIVE)
        {
            grid.getChildAt(index).setBackgroundColor(cellColorDefault)
            (grid.getChildAt(index) as TextView).setTextColor(cellTextColorDefault)
        }
        else if (newCellState == CELL_DISABLED)
        {
            grid.getChildAt(index).setBackgroundColor(cellColorDisabled)
            (grid.getChildAt(index) as TextView).setTextColor(cellTextColorDisabled)
        }
        else if (newCellState == CELL_UNSELECTED)
        {
            grid.getChildAt(index).setBackgroundColor(cellColorEmpty)
            (grid.getChildAt(index) as TextView).setTextColor(cellTextColorEmpty)
        }
        else if (newCellState == CELL_SELECTED)
        {
            grid.getChildAt(index).setBackgroundColor(cellColorSelected)
            (grid.getChildAt(index) as TextView).setTextColor(cellTextColorSelected)
        }
    }

    private fun updateCells()
    {
        for (r in 0..cells.size-1)
        {
            for (c in 0..cells[r].size-1)
            {
                val cell : TextView = grid.getChildAt(r * grid.columnCount + c) as TextView

                if (cells[r][c] == CELL_SELECTED)
                {
                    cell.setBackgroundColor(cellColorSelected)
                    cell.setTextColor(cellTextColorSelected)
                }
                else if (cells[r][c] == CELL_UNSELECTED)
                {
                    cell.setBackgroundColor(cellColorEmpty)
                    cell.setTextColor(cellTextColorEmpty)
                }
                else if (cells[r][c] == CELL_DISABLED)
                {
                    cell.setBackgroundColor(cellColorDisabled)
                    cell.setTextColor(cellTextColorDisabled)
                }
                else if (cells[r][c] == CELL_NOT_INTERACTIVE)
                {
                    cell.setBackgroundColor(cellColorDefault)
                    cell.setTextColor(cellTextColorDefault)
                }
            }
        }
    }

    fun setOnCellStateChangedListener(listener : OnCellStateChangedListener)
    {
        mListener = listener;
    }

    private fun getTime(hour : Float) : String
    {
        var h = (hour.toInt() % 12)
        if (h == 0) h = 12

        var time : String = h.toString() + ":" + decimalToTime(hour.toFloat() - hour.toInt())
        if (hour < 12) time += "am"
        else time += "pm"

        return time
    }

    private fun decimalToTime(decimal : Float) : String
    {
        var value = (decimal * 60).toString().split(".")[1]

        while (value.length < 2)
        {
            value = "0" + value
        }

        return value.substring(0, 2)
    }

    fun toggleCell(v : View) : Boolean?
    {
        val tag = v.getTag() as Int
        val row = tag / 100
        val col = tag % 100

        val toggled = toggleCellState(row, col)

        if (!toggled) return null

        val state = cells[row][col]
        if (state == CELL_SELECTED)
        {
            v.setBackgroundColor(cellColorSelected)
            (v as TextView).setTextColor(cellTextColorSelected)
            return true
        }
        else
        {
            v.setBackgroundColor(cellColorEmpty)
            (v as TextView).setTextColor(cellTextColorEmpty)
            return false
        }
    }

    fun selectCell(v : View)
    {
        val tag = v.getTag() as Int
        val row = tag / 100
        val col = tag % 100

        if (cells[row][col] == CELL_UNSELECTED)
        {
            cells[row][col] = CELL_SELECTED
            v.setBackgroundColor(cellColorSelected)
            (v as TextView).setTextColor(cellTextColorSelected)
        }
    }

    fun deselectCell(v : View)
    {
        val tag = v.getTag() as Int
        val row = tag / 100
        val col = tag % 100

        if (cells[row][col] == CELL_SELECTED)
        {
            cells[row][col] = CELL_UNSELECTED
            v.setBackgroundColor(cellColorEmpty)
            (v as TextView).setTextColor(cellTextColorEmpty)
        }
    }

    private fun toggleCellState(row : Int, col : Int) : Boolean
    {
        val state = cells[row][col]
        if (state == 0 || state == 1)
        {
            cells[row][col] = 1 - state
            return true
        }

        return false
    }

    private fun fillCellGrid(startR : Int, startC : Int, endR : Int, endC : Int, select : Boolean)
    {
        if (startR < endR)
        {
            for (r in startR..endR)
            {
                if (startC < endC)
                {
                    for (c in startC..endC)
                    {
                        val textViewIndex = r * cells[0].size + c
                        val temp = grid.getChildAt(textViewIndex)
                        if (temp == null) continue
                        if (select) selectCell(temp)
                        else deselectCell(temp)
                    }
                }
                else
                {
                    for (c in startC downTo endC)
                    {
                        val textViewIndex = r * cells[0].size + c
                        if (select) selectCell(grid.getChildAt(textViewIndex))
                        else deselectCell(grid.getChildAt(textViewIndex))
                    }
                }
            }
        }
        else
        {
            for (r in startR downTo endC)
            {
                if (startC < endC)
                {
                    for (c in startC..endC)
                    {
                        val textViewIndex = r * cells[0].size + c
                        if (select) selectCell(grid.getChildAt(textViewIndex))
                        else deselectCell(grid.getChildAt(textViewIndex))
                    }
                }
                else
                {
                    for (c in startC downTo endC)
                    {
                        val textViewIndex = r * cells[0].size + c
                        if (select) selectCell(grid.getChildAt(textViewIndex))
                        else deselectCell(grid.getChildAt(textViewIndex))
                    }
                }
            }
        }
    }

    private fun checkPermissions(callbackId: Int, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(context, p) == PERMISSION_GRANTED
        }

        if (!permissions)
            ActivityCompat.requestPermissions(context as Activity, permissionsId, callbackId)
    }
}
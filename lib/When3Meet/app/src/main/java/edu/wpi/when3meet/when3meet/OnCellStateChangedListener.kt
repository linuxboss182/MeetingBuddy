package edu.wpi.when3meet.when3meet

/**
 * Created by jfakult on 2/22/18.
 */

abstract class OnCellStateChangedListener
{
    abstract fun selected(row : Int, col : Int)

    abstract fun deselected(row : Int, col : Int)
}
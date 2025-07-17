package com.example.hwaroak.ui.friend

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragItemTouchHelperCallback(
    private val adapter: FriendAdapter
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled(): Boolean {
        // 관리 모드일 때만 true
        return adapter.isManageMode
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false // 스와이프 삭제는 비활성화
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // 상하 드래그만 허용
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPos = viewHolder.adapterPosition
        val toPos = target.adapterPosition

        // 버튼 뷰들은 이동 금지
        if (adapter.getItemViewType(fromPos) != FriendAdapter.VIEW_TYPE_FRIEND ||
            adapter.getItemViewType(toPos) != FriendAdapter.VIEW_TYPE_FRIEND
        ) return false

        adapter.moveItem(fromPos, toPos)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // 스와이프 없음
    }
}

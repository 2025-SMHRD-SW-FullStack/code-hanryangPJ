import React from 'react'
import './NoticeDetailPage.css'
import { useNavigate } from 'react-router-dom'

// 공지사항 상세 글 페이지
const NoticeDetailPage = () => {

  const navigate = useNavigate();

  const back = () => {
    navigate('/notice');
  }

  return (
    <div>
      <h1>공지사항</h1>

      {/* 클릭 시 공지사항 페이지로 이동 */}
      <button className='noticeBtn' onClick={back}>목록으로</button>

      {/* 질문 제목 */}
      <h2></h2>

      {/* 작성자, 작성일 */}

      {/* 질문 본문 */}

    </div>
  )
}

export default NoticeDetailPage
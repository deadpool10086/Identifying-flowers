// MainFrm.cpp : implementation of the CMainFrame class
//

#include "stdafx.h"
#include "MyBrowser.h"
#include "MyBrowserView.h"
#include "MainFrm.h"

#include <wininet.h>
#include <UrlHist.h>
#include "shlwapi.h"
#include "shlobj.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

/////////////////////////////////////////////////////////////////////////////
// CMainFrame

IMPLEMENT_DYNCREATE(CMainFrame, CFrameWnd)

BEGIN_MESSAGE_MAP(CMainFrame, CFrameWnd)
	//{{AFX_MSG_MAP(CMainFrame)
		// NOTE - the ClassWizard will add and remove mapping macros here.
		//    DO NOT EDIT what you see in these blocks of generated code !
	ON_CBN_SELENDOK(AFX_IDW_TOOLBAR + 1,OnNewAddress)
	ON_COMMAND(IDOK, OnNewAddressEnter)	
	ON_COMMAND_RANGE(0xe00, 0xfff, OnFavorite)	
	ON_WM_CREATE()
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

static UINT indicators[] =
{
	ID_SEPARATOR,           // status line indicator
	ID_INDICATOR_CAPS,
	ID_INDICATOR_NUM,
	ID_INDICATOR_SCRL,
};

/////////////////////////////////////////////////////////////////////////////
// CMainFrame construction/destruction

CMainFrame::CMainFrame()
{
	// TODO: add member initialization code here
	
}

CMainFrame::~CMainFrame()
{
}

int CMainFrame::OnCreate(LPCREATESTRUCT lpCreateStruct)
{
	
	CImageList img;
	CString str;

	if (CFrameWnd::OnCreate(lpCreateStruct) == -1)
		return -1;

	if (!m_wndReBar.Create(this))
	{
		AfxMessageBox("Error in create toolbar");
		return -1;      // fail to create
	}
	if (!m_wndToolBar.CreateEx(this))
	{
		AfxMessageBox("Error in create toolbar");
		return -1;      // fail to create
	}

	//���ù���������
	m_wndToolBar.GetToolBarCtrl().SetButtonWidth(20, 100);
	m_wndToolBar.GetToolBarCtrl().SetExtendedStyle(TBSTYLE_EX_DRAWDDARROWS);

	img.Create(IDB_BITMAP1, 22, 0, RGB(255, 0, 255));
	m_wndToolBar.GetToolBarCtrl().SetHotImageList(&img);
	img.Detach();

	img.Create(IDB_BITMAP2, 22, 0, RGB(255, 0, 255));
	m_wndToolBar.GetToolBarCtrl().SetImageList(&img);
	img.Detach();

	m_wndToolBar.ModifyStyle(0, TBSTYLE_FLAT | TBSTYLE_TRANSPARENT);
	m_wndToolBar.SetButtons(NULL, 6);

	//����ÿ����������ť
	m_wndToolBar.SetButtonInfo(0, ID_GO_BACK, TBSTYLE_BUTTON, 0);
	str.LoadString(IDS_GO_BACK);
	m_wndToolBar.SetButtonText(0, str);

	m_wndToolBar.SetButtonInfo(1, ID_GO_FORWARD, TBSTYLE_BUTTON, 1);
	str.LoadString(IDS_GO_FORWARD);
	m_wndToolBar.SetButtonText(1, str);

	m_wndToolBar.SetButtonInfo(2, ID_GO_STOP, TBSTYLE_BUTTON, 2);
	str.LoadString(IDS_GO_STOP);
	m_wndToolBar.SetButtonText(2, str);

	m_wndToolBar.SetButtonInfo(3, ID_GO_REFRESH, TBSTYLE_BUTTON, 3);
	str.LoadString(IDS_REFRESH);
	m_wndToolBar.SetButtonText(3, str);

	m_wndToolBar.SetButtonInfo(4, ID_START_PAGE, TBSTYLE_BUTTON, 4);
	str.LoadString(IDS_STARTPAGE);
	m_wndToolBar.SetButtonText(4, str);

	m_wndToolBar.SetButtonInfo(5, ID_GO_SERACH, TBSTYLE_BUTTON, 5);
	str.LoadString(IDS_SEARCH);
	m_wndToolBar.SetButtonText(5, str);
	
	//���ù������ĳߴ�
	CRect rectToolBar;
	m_wndToolBar.GetItemRect(0, &rectToolBar);
	m_wndToolBar.SetSizes(rectToolBar.Size(), CSize(30,20));

	// ����һ����Ͽ���Ϊ��ַ��
	if (!m_wndAddress.Create(CBS_DROPDOWN | WS_CHILD, CRect(0, 0, 200, 120), this, AFX_IDW_TOOLBAR + 1))
	{
		TRACE0("Failed to create combobox\n");
		return -1;      // fail to create
	}

	//��ӹ���������ַ����rebar��
	m_wndReBar.AddBar(&m_wndToolBar);
	str.LoadString(IDS_ADDRESS);
	m_wndReBar.AddBar(&m_wndAddress, str, NULL, RBBS_FIXEDBMP | RBBS_BREAK);

	// �趨Rebar�е�ÿ���ߴ�
	REBARBANDINFO rbbi;
	rbbi.cbSize = sizeof(rbbi);
	rbbi.fMask = RBBIM_CHILDSIZE | RBBIM_IDEALSIZE | RBBIM_SIZE;
	rbbi.cxMinChild = rectToolBar.Width();
	rbbi.cyMinChild = rectToolBar.Height();
	rbbi.cx = rbbi.cxIdeal = rectToolBar.Width() * 9;
	m_wndReBar.GetReBarCtrl().SetBandInfo(0, &rbbi);
	rbbi.cxMinChild = 0;

	CRect rectAddress;
	rbbi.fMask = RBBIM_CHILDSIZE | RBBIM_IDEALSIZE;
	m_wndAddress.GetEditCtrl()->GetWindowRect(&rectAddress);
	rbbi.cyMinChild = rectAddress.Height() + 10;
	rbbi.cxIdeal = 200;
	m_wndReBar.GetReBarCtrl().SetBandInfo(2, &rbbi);

	m_wndToolBar.SetBarStyle(m_wndToolBar.GetBarStyle() |
		CBRS_TOOLTIPS | CBRS_FLYBY | CBRS_SIZE_FIXED);

	//����״̬��
	if (!m_wndStatusBar.Create(this) ||
		!m_wndStatusBar.SetIndicators(indicators,
		  sizeof(indicators)/sizeof(UINT)))
	{
		TRACE0("Failed to create status bar\n");
		return -1;      // fail to create
	}

	//���ɡ��ҵ��ղؼС��Ӳ˵�
	char            path[MAX_PATH];
	CMenu*          pMenu;

	// ��ʼ��ȥ���ڵĲ˵�
	pMenu=GetMenu()->GetSubMenu(4);
	TRACE("%d",pMenu->GetMenuItemCount());
	while(pMenu->DeleteMenu(0, MF_BYPOSITION));

	SHGetSpecialFolderPath(NULL, path, CSIDL_FAVORITES, FALSE);
    //����ҵ��ղز˵���
	BuildFavoritesMenu(path, 0, pMenu);
	return 0;
}

BOOL CMainFrame::PreCreateWindow(CREATESTRUCT& cs)
{
	if( !CFrameWnd::PreCreateWindow(cs) )
		return FALSE;
	// TODO: Modify the Window class or styles here by modifying
	//  the CREATESTRUCT cs

	return TRUE;
}

/////////////////////////////////////////////////////////////////////////////
// CMainFrame diagnostics

#ifdef _DEBUG
void CMainFrame::AssertValid() const
{
	CFrameWnd::AssertValid();
}

void CMainFrame::Dump(CDumpContext& dc) const
{
	CFrameWnd::Dump(dc);
}

#endif //_DEBUG

/////////////////////////////////////////////////////////////////////////////
// CMainFrame message handlers

void CMainFrame::OnNewAddress()
{
	// ����Ͽ�ѡ��һ��ʱ����Ӧ���µ�ѡ��
	CString str;
	m_wndAddress.GetLBText(m_wndAddress.GetCurSel(), str);
	((CMyBrowserView*)GetActiveView())->Navigate2(str, 0, NULL);
}

void CMainFrame::OnNewAddressEnter()
{
	// ����Ͽ�����һ����ַʱ����Ӧ���룻ͬʱ��¼������ĵ�ַ��ӵ���Ͽ�������
	CString str;
	m_wndAddress.GetEditCtrl()->GetWindowText(str);
	((CMyBrowserView*)GetActiveView())->Navigate2(str, 0, NULL);
	COMBOBOXEXITEM item;
	item.mask = CBEIF_TEXT;
	item.iItem = -1;
	item.pszText = (LPTSTR)(LPCTSTR)str;
	m_wndAddress.InsertItem(&item);
}

int CMainFrame::BuildFavoritesMenu(LPCTSTR pszPath, int nStartPos, CMenu *pMenu)
{
	CString         strPath(pszPath);
	CString         strPath2;
	CString         str;
	WIN32_FIND_DATA wfd;
	HANDLE          h;
	int             nPos;
	int             nEndPos;
	int             nNewEndPos;
	int             nLastDir;
	TCHAR           buf[400];
	CStringArray    astrFavorites;
	CStringArray    astrDirs;
	CMenu*          pSubMenu;

	// ȷ����'\\'�ַ�
	if(strPath[strPath.GetLength() - 1] != _T('\\'))
		strPath += _T('\\');
	strPath2 = strPath;
	strPath += "*.*";

	// ����ɨ�� .URL Ȼ��ɨ����Ŀ¼
	h = FindFirstFile(strPath, &wfd);
	if(h != INVALID_HANDLE_VALUE)
	{
		nEndPos = nStartPos;
		do
		{
			//��Ŀ¼�ļ������������ԣ���ϵͳ����
			if((wfd.dwFileAttributes & (FILE_ATTRIBUTE_DIRECTORY|FILE_ATTRIBUTE_HIDDEN|FILE_ATTRIBUTE_SYSTEM))==0)
			{
				str = wfd.cFileName;
				if(str.Right(4) == _T(".url"))
				{
					// an .URL file is formatted just like an .INI file, so we can
					// use GetPrivateProfileString() to get the information we want
					::GetPrivateProfileString(_T("InternetShortcut"), _T("URL"),
											  _T(""), buf, 400,
											  strPath2 + str);
					str = str.Left(str.GetLength() - 4);

					// scan through the array and perform an insertion sort
					// to make sure the menu ends up in alphabetic order
					for(nPos = nStartPos ; nPos < nEndPos ; ++nPos)
					{
						if(str.CompareNoCase(astrFavorites[nPos]) < 0)
							break;
					}
					astrFavorites.InsertAt(nPos, str);
					m_astrFavoriteURLs.InsertAt(nPos, buf);
					++nEndPos;
				}
			}
		} while(FindNextFile(h, &wfd));
		FindClose(h);
		// Now add these items to the menu
		for(nPos = nStartPos ; nPos < nEndPos ; ++nPos)
		{
			pMenu->AppendMenu(MF_STRING | MF_ENABLED, 0xe00 + nPos, astrFavorites[nPos]);
		}


		// now that we've got all the .URL files, check the subdirectories for more
		nLastDir = 0;
		h = FindFirstFile(strPath, &wfd);
		ASSERT(h != INVALID_HANDLE_VALUE);
		do
		{
			if(wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
			{
				// ignore the current and parent directory entries
				if(lstrcmp(wfd.cFileName, _T(".")) == 0 || lstrcmp(wfd.cFileName, _T("..")) == 0)
					continue;

				for(nPos = 0 ; nPos < nLastDir ; ++nPos)
				{
					if(astrDirs[nPos].CompareNoCase(wfd.cFileName) > 0)
						break;
				}
				pSubMenu = new CMenu;
				pSubMenu->CreatePopupMenu();

				// call this function recursively.
				nNewEndPos = BuildFavoritesMenu(strPath2 + wfd.cFileName, nEndPos, pSubMenu);
				if(nNewEndPos != nEndPos)
				{
					// only intert a submenu if there are in fact .URL files in the subdirectory
					nEndPos = nNewEndPos;
					pMenu->InsertMenu(nPos, MF_BYPOSITION | MF_POPUP | MF_STRING, (UINT)pSubMenu->m_hMenu, wfd.cFileName);
					pSubMenu->Detach();
					astrDirs.InsertAt(nPos, wfd.cFileName);
					++nLastDir;
				}
				delete pSubMenu;
			}
		} while(FindNextFile(h, &wfd));
		FindClose(h);
	}
	return nEndPos;
}

void CMainFrame::OnFavorite(UINT nID)
{
	((CMyBrowserView*)GetActiveView())->Navigate2(m_astrFavoriteURLs[nID-0xe00], 0, NULL);
}

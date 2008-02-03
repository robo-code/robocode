using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Drawing.Drawing2D;
using System.Windows.Forms;
using java.util;
using nrobocodeui.manager;
using nrobocodeui.utils;
using robocode.battle;
using robocode.battlefield;
using robocode.manager;
using robocode.peer;
using robocode.ui;
using nrobocodeui.dialog;
using nrobocodeui.gfx;
using Random = System.Random;

namespace nrobocodeui.battleview
{
    public partial class BattleView : UserControl, IBattleView
    {
        #region Construction

        public BattleView()
        {
            this.battleField = new DefaultBattleField(800, 600);
            InitializeComponent();
        }

        public void InitBattleView(RobocodeManager manager, RobocodeFrame robocodeFrame)
        {
            this.manager = manager;
            this.robocodeFrame = robocodeFrame;
            battleViewProxy = new BattleViewProxy(this, robocodeFrame);
            imageManager = (ImageManager)manager.getImageManager();

            SetScale();
        }

        #endregion

        #region Private variables

        private RobocodeManager manager;
        private ImageManager imageManager;
        private RobocodeFrame robocodeFrame;
        private BattleViewProxy battleViewProxy;
        public BattleViewProxy BattleViewProxy
        {
            get
            {
                return battleViewProxy;
            }
        }

        private BattleField battleField;
        private Battle battle;

        #endregion

        #region Drawing variables

        private bool displayTPS;
        private bool displayFPS;
        private bool drawRobotName;
        private bool drawRobotEnergy;
        private bool drawScanArcs;
        private bool drawGround;
        private bool drawExplosions;
        private bool drawExplosionDebris;
        //TODO renderingHints = props.getRenderingHints();
        //TODO numBuffers = props.getOptionsRenderingNoBuffers();

        private double scale = 1.0;

        #endregion


        #region IBattleView Members

        public void setBattleField(BattleField bf)
        {
            battleField = bf;
        }

        public void setVisible(bool b)
        {
            //TODO
        }

        public void setInitialized(bool b)
        {
            //TODO
        }

        public void setBattle(Battle b)
        {
            battle = b;
        }

        public void repaint()
        {
            Invalidate();
        }


        public void setDisplayOptions()
        {
            RobocodeProperties props = manager.getProperties();

            displayTPS = props.getOptionsViewTPS();
            displayFPS = props.getOptionsViewFPS();
            drawRobotName = props.getOptionsViewRobotNames();
            drawRobotEnergy = props.getOptionsViewRobotEnergy();
            drawScanArcs = props.getOptionsViewScanArcs();
            drawGround = props.getOptionsViewGround();
            drawExplosions = props.getOptionsViewExplosions();
            drawExplosionDebris = props.getOptionsViewExplosionDebris();

            //TODO renderingHints = props.getRenderingHints();
            //TODO numBuffers = props.getOptionsRenderingNoBuffers();
        }

        public void update()
        {
            if (robocodeFrame.isIconified() || (getWidth() <= 0) || (getHeight() <= 0))
            {
                return;
            }
            Invalidate();
        }

        public bool isDisplayTPS()
        {
            return displayTPS;
        }

        public bool isDisplayFPS()
        {
            return displayFPS;
        }

        public int getWidth()
        {
            return Width;
        }

        public int getHeight()
        {
            return Height;
        }

        #endregion

        #region Helpers

        private void SetScale()
        {
            if ((getWidth() < battleField.getWidth() || getHeight() < battleField.getHeight()))
            {
                scale = Math.Min((double)getWidth() / battleField.getWidth(), (double)getHeight() / battleField.getHeight());
            }
            else
            {
                scale = 1;
            }
        }

        #endregion

        #region Events

        protected override void OnClientSizeChanged(EventArgs e)
        {
            base.OnClientSizeChanged(e);
            SetScale();
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            if (battle != null && battle.isRunning())
            {
                //TODO ? update();
                DrawBattle(e.Graphics);
            }
            else
            {
                paintRobocodeLogo(e.Graphics);
            }
        }

        #endregion

        #region Draw root

        private void DrawBattle(Graphics g)
        {
            DrawGround(g);
            DrawRobots(g);
        }

        #endregion

        #region Robots

        public void DrawRobots(Graphics g)
        {
            List robots = battle.getRobots();
            object[] robotPeers = robots.toArray();

            foreach (RobotPeer r in robotPeers)
            {
                DrawRobot(r, g);
            }
        }

        private void DrawRobot(RobotPeer robot, Graphics g)
        {
            if (robot.isAlive())
            {
                float x = (float)robot.getX();
                float y = battleField.getHeight() - (float)robot.getY();
                bool droid = robot.isDroid();

                Matrix at;
                at = new Matrix();
                at.Translate(x, y);
                at.Rotate(Radians.ToDegrees(robot.getHeading()));

                RenderImage robotRenderImage = new RenderImage(imageManager.getColoredBodyRenderImage(robot.getBodyColor()));

                robotRenderImage.Transform = at;
                robotRenderImage.Paint(g);

                at = new Matrix();
                at.Translate(x, y);
                at.Rotate(Radians.ToDegrees(robot.getGunHeading()));

                RenderImage gunRenderImage = new RenderImage(imageManager.getColoredGunRenderImage(robot.getGunColor()));

                gunRenderImage.Transform = at;
                gunRenderImage.Paint(g);

                if (!droid)
                {
                    at = new Matrix();
                    at.Translate(x, y);
                    at.Rotate(Radians.ToDegrees(robot.getRadarHeading()));

                    RenderImage radarRenderImage = new RenderImage(imageManager.getColoredRadarRenderImage(robot.getRadarColor()));

                    radarRenderImage.Transform = at;
                    radarRenderImage.Paint(g);
                }
            }
        }

        #endregion

        #region Ground

        private void DrawGround(Graphics g)
        {
            if (!drawGround)
            {
                g.Clear(Color.Black);
            }
            else
            {
                // Create pre-rendered ground image if it is not available
                if (groundImage == null)
                {
                    CreateGroundImage();
                }

                // Draw the pre-rendered ground if it is available
                if (groundImage != null)
                {
                    int groundWidth = (int)(battleField.getWidth() * scale) + 1;
                    int groundHeight = (int)(battleField.getHeight() * scale) + 1;

                    int dx = (getWidth() - groundWidth) / 2;
                    int dy = (getHeight() - groundHeight) / 2;

                    //                    AffineTransform savedTx = g.getTransform();

                    //                    g.setTransform(new AffineTransform());

                    g.DrawImageUnscaled(groundImage, 0, 0);

                    //                    g.setTransform(savedTx);
                }
            }
        }

        private Bitmap groundImage;
        private int[,] groundTiles;
        private int groundTileWidth = 64;
        private int groundTileHeight = 64;

        private void CreateGroundImage()
        {
            // Reinitialize ground tiles

            int NUM_HORZ_TILES = battleField.getWidth() / groundTileWidth + 1;
            int NUM_VERT_TILES = battleField.getHeight() / groundTileHeight + 1;

            if ((groundTiles == null) || (groundTiles.GetLength(0) != NUM_HORZ_TILES) || (groundTiles.GetLength(1) != NUM_VERT_TILES))
            {
                groundTiles = new int[NUM_HORZ_TILES, NUM_VERT_TILES];

                Random random = new Random();

                for (int y = NUM_VERT_TILES - 1; y >= 0; y--)
                {
                    for (int x = NUM_HORZ_TILES - 1; x >= 0; x--)
                    {
                        groundTiles[x, y] = random.Next(5);
                    }
                }
            }

            // Create new buffered image with the ground pre-rendered

            int groundWidth = (int)(battleField.getWidth() * scale);
            int groundHeight = (int)(battleField.getHeight() * scale);

            groundImage = new Bitmap(groundWidth, groundHeight, PixelFormat.Format24bppRgb);

            Graphics groundGfx = Graphics.FromImage(groundImage);

            // TODO: Scale the gfx on the bitmap

            for (int y = NUM_VERT_TILES - 1; y >= 0; y--)
            {
                for (int x = NUM_HORZ_TILES - 1; x >= 0; x--)
                {
                    Image img = resources.images.ground.blue_metal[groundTiles[x, y]];
                    if (img != null)
                    {
                        groundGfx.DrawImage(img, new Rectangle(x * groundTileWidth, y * groundTileHeight, img.Width, img.Height), 0, 0, img.Width, img.Height, GraphicsUnit.Pixel);
                    }
                }
            }
        }

        #endregion

        #region Logo

        public void paintRobocodeLogo(Graphics g)
        {
            //TODO
        }

        #endregion

    }
}

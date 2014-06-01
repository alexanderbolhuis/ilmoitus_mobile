//
//  FirstViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "MyDeclarationViewController.h"
#import "Declaration.h"
#import "constants.h"

@interface MyDeclarationViewController ()
@property (nonatomic, strong) NSMutableArray *declarationList;
@property (weak, nonatomic) IBOutlet UINavigationItem *navItem;

@end

@implementation MyDeclarationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // Init Refresh
    self.refreshControl = [[UIRefreshControl alloc] init];
    [self.refreshControl addTarget:self action:@selector(refreshInvoked:forState:) forControlEvents:UIControlEventValueChanged];
    
	// Do any additional setup after loading the view, typically from a nib.
    self.navItem.title = [NSString stringWithFormat:@"Ingelogd als %@ %@ (%@)", [[NSUserDefaults standardUserDefaults] stringForKey:@"person_first_name"], [[NSUserDefaults standardUserDefaults] stringForKey:@"person_last_name"], [[NSUserDefaults standardUserDefaults] stringForKey:@"person_employee_number"]];
    
    // GET Actions when shwoing view
    [self declarationsFromServer];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    self.navigationController.navigationBar.hidden = NO;
    // GET Actions when showing view
    [self declarationsFromServer];
    
}

// For refreshing tableview
-(void) refreshInvoked:(id)sender forState:(UIControlState)state {
    [self declarationsFromServer];
}

- (void)declarationsFromServer
{
    // Do Request
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:@"%@/declarations/employee", baseURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSMutableArray *declarationsFound = [[NSMutableArray alloc] init];
        for (NSDictionary *decl in json) {
                Declaration *declaration = [[Declaration alloc] init];
                declaration.status = decl[@"state"];
            
                // TODO get totalprice from json data
                declaration.itemsTotalPrice = 10.00;
                // declaration.itemsTotalPrice = decl[@"items_total_price"];
                NSDateFormatter *formatter = [NSDateFormatter new];
                formatter.dateFormat = @"yyyy-MM-dd' 'HH:mm:ss.S";
            
                NSDate *date = [formatter dateFromString:decl[@"created_at"]];
                [formatter setDateFormat:@"dd-MM-yyyy"];
                declaration.createdAt = [formatter stringFromDate:date];
                [declarationsFound addObject:declaration];
        }
        
        [_declarationList removeAllObjects];
        _declarationList = declarationsFound;
        
        NSLog(@"GET request success response: %@", json);
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"GET request Error: %@", error);
    }];
    
    // Reload the data
    [self.tableView reloadData];
    [self.refreshControl endRefreshing];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Only one section
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return number of actions in actionlist
    return [self.declarationList count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:  (NSIndexPath *)indexPath
{
    // View for TableViewCell
    static NSString *CellIdentifier = @"declaration";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    Declaration *declaration = [self.declarationList     objectAtIndex:indexPath.row];
    
    UILabel *createdAtlabel;
    
    createdAtlabel = (UILabel *)[cell viewWithTag:1];
    [createdAtlabel adjustsFontSizeToFitWidth];
    createdAtlabel.text = [NSString stringWithFormat:@"Declaratie op %@", declaration.createdAt];
    
    UILabel *statusLabel;
    
    statusLabel = (UILabel *)[cell viewWithTag:2];
    [statusLabel adjustsFontSizeToFitWidth];
    statusLabel.text = declaration.status;
    
    UILabel *amountLabel;
    
    amountLabel = (UILabel *)[cell viewWithTag:3];
    [amountLabel adjustsFontSizeToFitWidth];
    NSString* formattedAmount = [NSString stringWithFormat:@"%.02f", declaration.itemsTotalPrice];
    amountLabel.text = [NSString stringWithFormat:@"€%@", formattedAmount];
    
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    return @"Declaraties";
}

@end

//
//  SecondViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationViewController.h"
#import "NewDeclarationLineViewController.h"
#import "Declaration.h"
#import "DeclarationLine.h"
#import "Supervisor.h"
#import "constants.h"
#import "Attachment.h"

@interface NewDeclarationViewController ()
@property (weak, nonatomic) IBOutlet UITextField *supervisor;
@property (weak, nonatomic) NSMutableArray *supervisorList;
@property (weak, nonatomic) IBOutlet UITextView *comment;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@end

@implementation NewDeclarationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    _supervisor.delegate = self;
    [_comment setReturnKeyType: 	UIReturnKeyDone];
    _comment.delegate = self;
    _tableView.delegate = self;
    _tableView.dataSource = self;
    
    [self getSupervisorList];
    
    if (_declaration == nil) {
        _declaration = [[Declaration alloc] init];
    }
    _comment.text = _declaration.comment;
}

- (IBAction)postDeclaration:(id)sender {
    _declaration.className = @"open_declaration";
    _declaration.status = @"Open";
        // TODO get current date in right format
    _declaration.createdAt = @"2014-05-15 07:27:33.448849";
    _declaration.createdBy = [[[NSUserDefaults standardUserDefaults] stringForKey:@"person_id"] longLongValue];
    
    // TODO Get supervisor from dropdown
    NSMutableArray *at = [[NSMutableArray alloc]init];
    [at addObject:[NSNumber numberWithLongLong:[[[NSUserDefaults standardUserDefaults] stringForKey:@"supervisor"] longLongValue]]];
    _declaration.assignedTo = at;
    _declaration.comment = _comment.text;
    // TODO Calc price and items
    _declaration.itemsTotalPrice = 30.00;
    _declaration.itemsCount = 2;
    [self saveDeclaration];
}

-(IBAction)unwindToNewDeclaration:(UIStoryboardSegue *)segue
{
    NewDeclarationLineViewController * source = [segue sourceViewController];
    if(source.declarationLine != nil)
    {
        [_declaration.lines addObject:source.declarationLine];
        [self.tableView reloadData];
    }
    if(source.attachment != nil)
    {
        [_declaration.attachments addObject:source.attachment];
    }
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

-(BOOL)textViewShouldEndEditing:(UITextView *)textView {
    _declaration.comment = _comment.text;
    return YES;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    
    return YES;
}

- (IBAction)cancelDeclaration:(id)sender {
    _declaration = nil;
    [_tableView reloadData];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)saveDeclaration
{
    Declaration *decl = _declaration;
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    // Declaration
    NSDictionary *declaration = @{@"state":decl.status, @"created_at":decl.createdAt, @"created_by":[NSNumber numberWithLongLong:decl.createdBy], @"assigned_to":decl.assignedTo, @"comment":decl.comment, @"items_total_price":[NSNumber numberWithFloat:decl.itemsTotalPrice], @"items_count":[NSNumber numberWithInt:decl.itemsCount]};
    
    // Lines
    NSMutableArray *declarationlines = [[NSMutableArray alloc] init];
    for (DeclarationLine *line in decl.lines)
    {
        NSDictionary *currentline = @{@"receipt_date": line.date, @"cost":[NSNumber numberWithFloat:line.cost], @"declaration_sub_type":[NSNumber numberWithLongLong:line.subtype]};
        [declarationlines addObject:currentline];
    }
    
    // Attachments
    NSMutableArray *attachments = [[NSMutableArray alloc] init];
    for (Attachment *attachment in _declaration.attachments)
    {
        NSDictionary *currentAttachment = @{@"name":attachment.name, @"file":attachment.data};
        [attachments addObject:currentAttachment];
    }
    
    // Total dict
    NSDictionary *params = @{@"declaration":declaration, @"lines":declarationlines, @"attachments":attachments};
    
    NSLog(@"JSON data that is going to be saved/sent: %@",params);
    
    NSString *url = [NSString stringWithFormat:@"%@/declaration", baseURL];
    AFHTTPRequestOperation *apiRequest = [manager POST:url parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"JSON response data for saving declaration: %@",json);
        // Handle success
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error while saving declaration: %@", error);
        // Handle error
        
    }];
    
    [apiRequest start];
}

-(void)getSupervisorList
{
    // Do Request
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    NSString *url = [NSString stringWithFormat:@"%@/supervisors/", baseURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"JSON response: %@", json);
        
        NSMutableArray *supervisorsFound = [[NSMutableArray alloc] init];
        for (NSDictionary *supervisor in json) {
            Supervisor *sup = [[Supervisor alloc] init];
            sup.class_name = supervisor[@"class_name"];
            sup.first_name = supervisor[@"first_name"];
            sup.last_name = supervisor[@"last_name"];
            sup.email = supervisor[@"email"];
            sup.employee_number = [supervisor[@"employee_number"] integerValue];
            sup.department = [supervisor[@"department"] longLongValue];
            sup.supervisor = [supervisor[@"supervisor"] longLongValue];
            sup.max_declaration_price = [supervisor[@"max_declaration_price"] floatValue];
            
            
            if ([supervisor[@"id"] longLongValue] == [[[NSUserDefaults standardUserDefaults] stringForKey:@"supervisor"] longLongValue]) {
                _supervisor.text = [NSString stringWithFormat:@"%@ %@", sup.first_name, sup.last_name];
            }
            
            
            [supervisorsFound addObject:sup];
        }
        _supervisorList = supervisorsFound;
        
        // TODO create dropdown to select supervisor
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error while getting supervisor list: %@", error);
    }];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [_declaration.lines count];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier];
    }
    
    DeclarationLine *line = [_declaration.lines objectAtIndex:indexPath.row];
    
    cell.textLabel.text = [NSString stringWithFormat:@"%@ - %f/2",@"DecalrationSubType", line.cost];
    
    return cell;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [_declaration.lines removeObjectAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}

- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return NO;
}
@end
